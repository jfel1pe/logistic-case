package com.logistic.logistic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "The Username is required")
    private String username;

    @NotBlank(message = "The Password is required")
    private String password;
}
