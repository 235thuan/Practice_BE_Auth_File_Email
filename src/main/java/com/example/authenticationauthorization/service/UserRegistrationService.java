package com.example.authenticationauthorization.service;

import com.example.authenticationauthorization.configuration.exception.RunTimeException.UserAlreadyExistsException;

import com.example.authenticationauthorization.dto.UserDTO;
import com.example.authenticationauthorization.mapper.UserDTOMapper;
import com.example.authenticationauthorization.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;


@Service
public class UserRegistrationService {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final UserDTOMapper userDTOMapper = UserDTOMapper.INSTANCE;
    private final EmailService emailService;

    public UserRegistrationService(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    @Transactional
    public User registerUser(UserDTO userDTO) {
        String token = generateVerificationToken();
        String email = userDTO.getEmail();
//
        if (userService.existsByUserName(userDTO.getUserName())) {
            throw new UserAlreadyExistsException("User with username " + userDTO.getUserName() + " already exists");
        }

        User user = userDTOMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roleService.getDefaultRole());
        user.setVerificationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusDays(1));

        emailService.sendVerificationEmail(email, token);

        return userService.save(user);
    }


    private String generateVerificationToken() {
        // Your logic to generate a unique verification token
        return UUID.randomUUID().toString();
    }

}
