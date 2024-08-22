package com.example.authenticationauthorization.controller;

import com.example.authenticationauthorization.dto.*;
import com.example.authenticationauthorization.service.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthsController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    private final UserRegistrationService userRegistrationService;

    private final TokenServiceRedis tokenServiceRedis;

    public AuthsController(AuthenticationService authenticationService,
                           UserService userService,
                           UserRegistrationService userRegistrationService,
                           TokenServiceRedis tokenServiceRedis) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userRegistrationService = userRegistrationService;
        this.tokenServiceRedis = tokenServiceRedis;
    }


    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO,
                                                  @RequestHeader("Device-ID") String deviceId) {
        try {
            String username = loginRequestDTO.getUsername();
            // Authenticate user
            Authentication authentication = authenticationService.authenticate(
                    loginRequestDTO.getUsername(),
                    loginRequestDTO.getPassword()
            );

            // khác thiết bị đã login
            if (tokenServiceRedis.isLoggedInOnDifferentDevice(username, deviceId)) {
                //gui mail thong bao dang nhap
                userService.warningLogin(username);
                return new ResponseEntity<>(HttpStatus.valueOf("Already login in different device"));
            }

            //redis cùng thiết bị đã login
            if (tokenServiceRedis.isLoggedIn(username, deviceId)) {
                return new ResponseEntity<>(HttpStatus.valueOf("Already login"));
            }
            // Generate token
            String token = tokenServiceRedis.generateToken(authentication, deviceId);
            // Lưu token vào Redis
            long expirationTimeInSeconds = tokenServiceRedis.getExpirationTimeInSeconds(token);
            tokenServiceRedis.saveToken(username, deviceId, token, expirationTimeInSeconds);
            // Create response DTO
            LoginResponseDTO responseDto = new LoginResponseDTO(username, token);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestBody LogoutRequestDTO logoutRequestDTO,
                                                    @RequestHeader("Device-ID") String deviceId) {
        String username = logoutRequestDTO.getUsername();

        if (deviceId == null || deviceId.isEmpty()) {
            return new ResponseEntity<>(new ResponseDTO("Device Id required","404","false"), HttpStatus.BAD_REQUEST);
        }

        String token = tokenServiceRedis.getToken(username, deviceId);

        try {

            boolean isTokenBlacklisted = tokenServiceRedis.isTokenBlacklisted(token);
            if (!isTokenBlacklisted) {
                long ttl = tokenServiceRedis.getTokenTTL(token);
                if (ttl > 0) {
                    tokenServiceRedis.addToBlacklist(token, ttl);
                }
            }
            tokenServiceRedis.deleteToken(username, deviceId);
            tokenServiceRedis.deleteAllTokensForUser(username);
            return new ResponseEntity<>(new ResponseDTO("Logout successful","200","true"), HttpStatus.OK);
        } catch (Exception e) {
            // Log lỗi và trả về phản hồi lỗi cho client
            return new ResponseEntity<>(new ResponseDTO("Logout error during process","404","false"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("register")
    public ResponseEntity<ResponseDTO> register(@RequestBody UserDTO userDTO) {
        try {
            userRegistrationService.registerUser(userDTO);
            return new ResponseEntity<>(new ResponseDTO("Register successful","200","true"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("error register "+e.getMessage(),"404","false"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseDTO> verifyUser(@RequestParam String token) {
        try {
            boolean isVerified = userService.verifyToken(token);
            if (isVerified) {
                return new ResponseEntity<>(new ResponseDTO("User verified","200","true"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseDTO("User not verified","404","false"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("Verify error " + e.getMessage(),"404","false"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    //gui mail reset pass
    @PostMapping("/request-password-reset")
    public ResponseEntity<ResponseDTO> requestPasswordReset(@RequestBody PasswordResetRequestDTO resetRequestDTO) {
        try {
            userService.generateResetToken(resetRequestDTO.getEmail());
            return new ResponseEntity<>(new ResponseDTO("Password reset email sent","200","true"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("Reset password error " + e.getMessage(),"404","false"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ResponseDTO> validateResetToken(@RequestParam String token) {
        boolean isValid = userService.isResetTokenValid(token);
        if (isValid) {
            return new ResponseEntity<>(new ResponseDTO("Token is valid","200","true"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO("Token not found or invalid!","404","false"), HttpStatus.BAD_REQUEST);
        }
    }
    //dat lai password
    @PutMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean isUpdated = userService.updatePassword(token, newPassword);
        if (isUpdated) {
            return new ResponseEntity<>(new ResponseDTO("Password reset successful","200","true"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO("Error process reset password!","404","false"), HttpStatus.NOT_FOUND);
        }
    }

    //get list sort by create_at new to old
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();

            users.forEach(System.out::println);
            if (users.isEmpty()) {
                // Return 204 No Content if no users are found
                return new ResponseEntity<>(new ResponseDTO("No users found!","404","false"), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO("Error getting all users "+e.getMessage(),"404","false");
            // Return 500 Internal Server Error with the error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        try {
            Optional<UserResponseDTO> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .build() // 404 Not Found
                    );
        } catch (Exception e) {

            return new ResponseEntity<>(new UserResponseDTO("Error find get user "+e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserResponseDTO userDetails) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userDetails);
            System.out.println(updatedUser);
            if (updatedUser.getUserName() == null || updatedUser.getUserName().isEmpty()) {
                return new ResponseEntity<>(new UserResponseDTO("Error update user"),HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }

        } catch (RuntimeException e) {
            return new ResponseEntity<>(new UserResponseDTO("Error update user "+e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    // Soft  Delete a user
    @PutMapping("/softdel/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable Long id) {
        try {
            userService.softDeleteUser(id);
            return new ResponseEntity<>(new ResponseDTO("Delete successful","200","true"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("Error softdelete " + e.getMessage(),"404","false"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
