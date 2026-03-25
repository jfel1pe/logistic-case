package com.logistic.logistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDTO {

    private Integer id;

    @NotBlank(message = "The productType name is required.")
    @Size(max = 100, message = "The productType name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "The productType description is required.")
    @Size(max = 250, message = "The productType description cannot exceed 250 characters")
    private String description;
}
