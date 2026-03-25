package com.logistic.logistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Integer id;

    @NotBlank(message = "A document ID is required.")
    @Size(max = 45, message = "The document cannot exceed 45 characters")
    private String documentId;

    @NotBlank(message = "The client name is required.")
    @Size(max = 100, message = "The client name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "The client direction is required.")
    @Size(max = 100, message = "The client direction cannot exceed 100 characters")
    private String direction;

}
