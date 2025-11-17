package com.app.usermanagementservice.service;

import com.app.usermanagementservice.dto.UserRequest;
import com.app.usermanagementservice.dto.UserResponse;
import com.app.usermanagementservice.exceptions.EmailAlreadyExistsException;
import com.app.usermanagementservice.exceptions.UserNotFoundException;
import com.app.usermanagementservice.model.UserEntity;
import com.app.usermanagementservice.model.UserRole;
import com.app.usermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.debug("Creating user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Attempt to create user with existing email: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = new UserEntity();
        mapRequestToEntity(request, user);
        UserEntity savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return mapToUserResponse(savedUser);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination");
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });

        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.debug("Updating user with ID: {}", id);
        
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });

        if (!existingUser.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            log.warn("Attempt to update user with existing email: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " already exists");
        }

        mapRequestToEntity(request, existingUser);
        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", id);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    private void mapRequestToEntity(UserRequest request, UserEntity entity) {
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        if (request.getRole() != null) {
            entity.setUserRole(request.getRole());
        } else {
            entity.setUserRole(UserRole.USER);
        }
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getUserRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setModifiedAt(user.getModifiedAt());
        return response;
    }
}
