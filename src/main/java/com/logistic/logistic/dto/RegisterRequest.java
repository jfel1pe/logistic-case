package com.logistic.logistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "The username is required")
    @Size(min = 4, max = 45, message = "El username debe tener entre 4 y 45 caracteres")
    private String username;

    @NotBlank(message = "The Password is required")
    @Size(min = 6, max = 100, message = "El password debe tener mínimo 6 caracteres")
    private String password;
}
