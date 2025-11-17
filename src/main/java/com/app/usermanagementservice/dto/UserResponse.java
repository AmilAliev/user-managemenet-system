package com.app.usermanagementservice.dto;

import com.app.usermanagementservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
