package com.logistic.logistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortDTO {

    private Integer id;
    @NotBlank(message = "The Port name is required.")
    @Size(max = 100, message = "The Warehouse name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "The Port country is required.")
    @Size(max = 45, message = "The Warehouse country cannot exceed 45 characters")
    private String country;

    @NotBlank(message = "The Port ubication is required.")
    @Size(max = 45, message = "The Warehouse ubication cannot exceed 45 characters")
    private String ubication;

    @NotNull(message = "This field Port international is required.")
    private Boolean international;
}
