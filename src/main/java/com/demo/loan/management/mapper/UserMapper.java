package com.demo.loan.management.mapper;

import com.demo.loan.management.dto.RegisterRequest;
import com.demo.loan.management.dto.UserDTO;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Logger for exception handling
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    // Converts RegisterRequest to User entity
    public User toEntity(RegisterRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("RegisterRequest cannot be null.");
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());

            // Validate and set role, default to USER if null or invalid
            if (request.getRole() == null) {
                user.setRole(Role.USER);  // Default to USER
            } else {
                try {
                    user.setRole(request.getRole());
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid role provided: {}", request.getRole());
                    user.setRole(Role.USER);
                }
            }

            return user;
        } catch (Exception e) {
            logger.error("Error mapping RegisterRequest to User: {}", e.getMessage());
            throw new RuntimeException("Error mapping RegisterRequest to User", e);
        }
    }

    // Converts User entity to UserDTO
    public UserDTO toDTO(User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null.");
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setAddress(user.getAddress());

            // Convert Role enum to String and handle null role
            if (user.getRole() != null) {
                userDTO.setRole(user.getRole().name()); // Convert enum to String
            } else {
                userDTO.setRole(null);
            }

            return userDTO;
        } catch (Exception e) {
            logger.error("Error mapping User to UserDTO: {}", e.getMessage());
            throw new RuntimeException("Error mapping User to UserDTO", e);
        }
    }
}
