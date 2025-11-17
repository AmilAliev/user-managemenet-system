package com.app.usermanagementservice.service;

import com.app.usermanagementservice.dto.UserRequest;
import com.app.usermanagementservice.dto.UserResponse;
import com.app.usermanagementservice.exceptions.EmailAlreadyExistsException;
import com.app.usermanagementservice.exceptions.UserNotFoundException;
import com.app.usermanagementservice.model.UserEntity;
import com.app.usermanagementservice.model.UserRole;
import com.app.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setPhone("1234567890");
        userEntity.setUserRole(UserRole.USER);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setModifiedAt(LocalDateTime.now());

        userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPhone("1234567890");
        userRequest.setRole(UserRole.USER);
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals(userEntity.getId(), response.getId());
        assertEquals(userEntity.getName(), response.getName());
        assertEquals(userEntity.getEmail(), response.getEmail());
        assertEquals(userEntity.getPhone(), response.getPhone());
        assertEquals(userEntity.getUserRole(), response.getRole());

        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(userRequest);
        });

        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_DefaultRole() {
        userRequest.setRole(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setModifiedAt(LocalDateTime.now());
            return entity;
        });

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testGetAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity), pageable, 1);
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> response = userService.getAllUsers(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(userEntity.getId(), response.getContent().get(0).getId());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(userEntity.getId(), response.getId());
        assertEquals(userEntity.getName(), response.getName());
        assertEquals(userEntity.getEmail(), response.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        verify(userRepository).findById(1L);
    }

    @Test
    void testUpdateUser_Success() {
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setEmail("john.doe@example.com");
        updateRequest.setPhone("9876543210");
        updateRequest.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(1L, userRequest);
        });

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        UserRequest updateRequest = new UserRequest();
        updateRequest.setEmail("new.email@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("new.email@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(1L, updateRequest);
        });

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("new.email@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_SameEmail() {
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("john.doe@example.com");
        updateRequest.setPhone("1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}

