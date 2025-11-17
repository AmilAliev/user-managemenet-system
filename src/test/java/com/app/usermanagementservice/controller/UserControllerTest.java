package com.app.usermanagementservice.controller;

import com.app.usermanagementservice.dto.UserRequest;
import com.app.usermanagementservice.dto.UserResponse;
import com.app.usermanagementservice.model.UserRole;
import com.app.usermanagementservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPhone("1234567890");
        userRequest.setRole(UserRole.USER);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setPhone("1234567890");
        userResponse.setRole(UserRole.USER);
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setModifiedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testCreateUser_ValidationError() throws Exception {
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setEmail("invalid-email"); // Invalid: not a valid email

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        Page<UserResponse> userPage = new PageImpl<>(
                List.of(userResponse),
                PageRequest.of(0, 10),
                1
        );

        when(userService.getAllUsers(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        userResponse.setName("Jane Doe");
        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void testUpdateUser_ValidationError() throws Exception {
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setName(""); // Invalid: blank name

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}

