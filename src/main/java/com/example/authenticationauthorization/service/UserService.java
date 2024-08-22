package com.example.authenticationauthorization.service;


import com.example.authenticationauthorization.configuration.exception.RunTimeException.AuthException;
import com.example.authenticationauthorization.configuration.exception.RunTimeException.RoleNotFoundException;
import com.example.authenticationauthorization.configuration.exception.RunTimeException.UserNotFoundException;
import com.example.authenticationauthorization.dto.RoleResponseForReturnDTO;
import com.example.authenticationauthorization.dto.UserResponseDTO;
import com.example.authenticationauthorization.enums.StatusENUM;
import com.example.authenticationauthorization.mapper.UserDTOMapper;
import com.example.authenticationauthorization.mapper.UserResponseDTOMapper;
import com.example.authenticationauthorization.model.Permission;
import com.example.authenticationauthorization.model.Role;
import com.example.authenticationauthorization.model.User;
import com.example.authenticationauthorization.repository.UserRepository;


import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseDTOMapper userResponseDTOMapper = UserResponseDTOMapper.INSTANCE;
    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Set<RoleResponseForReturnDTO> mapRolesToRoleResponseDTO(User user) {
        return user.getRoles().stream()
                .map(role -> {
                    role.getPermissions().forEach(permission -> {
                    });

                    Set<String> permissions = role.getPermissions().stream()
                            .map(Permission::getNamePermission)
                            .collect(Collectors.toSet());
                    return new RoleResponseForReturnDTO(role.getName(), permissions);
                })
                .collect(Collectors.toSet());
    }

    public boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean verifyToken(String token) {
        // Validate the token and find the corresponding user
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Check if user is not already verified
            if (!user.isVerified()) {
                user.setVerified(true);
                user.setVerificationToken(null); // Remove the token after verification
                userRepository.save(user); // Save the updated user
                return true; // Verification successful
            } else {
                // User is already verified
                return false;
            }
        } else {
            // User not found
            return false;
        }
    }

    @Async("taskExecutor")
    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour

        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void warningLogin(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username"));
        emailService.sendWarningLoginFromDifferentDevice(user.getEmail());
    }

    public boolean isResetTokenValid(String token) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getTokenExpiryDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public boolean updatePassword(String token, String newPassword) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getTokenExpiryDate().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetToken(null); // Remove the token after reset
                user.setTokenExpiryDate(null); // Clear expiry date
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Scheduled(fixedRate = 1000 * 60 * 60 * 72) // Thực hiện mỗi 72 giờ
    public void deleteUsersNotVerifiedOlderThan() {
        System.out.println("Running deleteUsersNotVerifiedOlderThan at: " + LocalDateTime.now());
        // ngay lam moc de xoa
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(72);
        userRepository.deleteUsersNotVerifiedOlderThan(cutoffDate);
    }

//get list sort by create_at new to old
    public List<UserResponseDTO> getAllUsers() {
        List<User> allUsers = userRepository.findAllByOrderByCreatedAtDesc();
        return allUsers.stream()
                .map(userResponseDTOMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

//get user by id
    public Optional<UserResponseDTO> getUserById(Long id) {
        Optional<User> userOptional = userRepository.getUserByUserId(id);
        return userOptional.map(userResponseDTOMapper::toUserResponseDTO);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserResponseDTO userDetails) {
        // Step 1: Fetch the existing user
        Optional<User> optionalUser = userRepository.getUserByUserId(id);

        // Step 2: Check if the user exists
        if (optionalUser.isPresent()) {
            // Get the existing user
            User existingUser = optionalUser.get();
            // Update user details
            existingUser.setUserName(userDetails.getUserName()); // Sử dụng đúng tên trường
            existingUser.setEmail(userDetails.getEmail()); // Sử dụng đúng tên trường

            // Save the updated user
            User updatedUser = userRepository.save(existingUser);

            // Convert updated user to DTO and return
            return userResponseDTOMapper.toUserResponseDTO(updatedUser);
        } else {
            // If user not found, throw an exception
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    @Transactional
    public void softDeleteUser(Long id) {
        User existingUser = userRepository.getUserByUserId(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        existingUser.setStatus(StatusENUM.DELETE.toString());
        userRepository.save(existingUser);
    }
}
