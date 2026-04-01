package com.example.sentinal_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "description must be at most 255 characters")
    private String description;

    private Boolean active;
}
