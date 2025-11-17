package com.app.usermanagementservice.controller;

import com.app.usermanagementservice.dto.UserRequest;
import com.app.usermanagementservice.dto.UserResponse;
import com.app.usermanagementservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
        log.info("Creating new user with email: {}", userRequest.getEmail());
        UserResponse createdUser = userService.createUser(userRequest);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        log.info("Retrieved {} users", users.getTotalElements());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        log.info("User found with ID: {}", id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("Updating user with ID: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        log.info("User updated successfully with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
