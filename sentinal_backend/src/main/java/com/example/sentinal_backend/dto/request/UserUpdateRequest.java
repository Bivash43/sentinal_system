package com.example.sentinal_backend.dto.request;

import com.example.sentinal_backend.model.UserRole;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
    private String password;

    private UserRole role;

    private Boolean enabled;
}
