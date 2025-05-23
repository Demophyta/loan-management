package com.demo.loan.management.service;

import com.demo.loan.management.dto.RegisterRequest;
import com.demo.loan.management.dto.UserDTO;
import com.demo.loan.management.mapper.UserMapper;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Registers a new user and returns a UserDTO.
     */
    public UserDTO saveUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // Create user object using mapper
        User user = userMapper.toEntity(request);

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save to DB
        user = userRepository.save(user);  // Save the User entity
        logger.info("User registered successfully: {}", user.getEmail());

        // Return the UserDTO
        return userMapper.toDTO(user);  // Convert User entity to UserDTO
    }

    /**
     * Fetches a user by their email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if a user already exists by email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Deletes a user by ID, only if the current user is an admin.
     */
    public void deleteUser(Long userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User currentUserDetails = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUserDetails.getRole() != Role.ADMIN) {
                throw new SecurityException("Only admins can delete users");
            }
        } else {
            throw new IllegalStateException("Authenticated user is not of type UserDetails");
        }


        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }

        userRepository.deleteById(userId);
        logger.info("User with ID {} deleted successfully.", userId);
    }
}
