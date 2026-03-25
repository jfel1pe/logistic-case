package com.logistic.logistic.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandShipmentDTO {

    // Datos del Shipment base
    private Integer id;
    @NotNull(message = "The client is required.")
    private Integer clientId;

    @NotNull(message = "The product is required.")
    private Integer productTypeId;

    @NotNull(message = "The Landshipment quantity is required.")
    @Min(value = 1, message = "The Landshipment quantity must be greater than 0")
    private Integer quantity;

    private LocalDateTime registryDate; // se genera automáticamente

    @NotNull(message = "The Landshipment deliveryDate is required.")
    private LocalDateTime deliveryDate;

    @NotNull(message = "The Landshipment price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "The Landshipment price must be greater than 0")
    private BigDecimal price;

    // se genera automáticamente
    private String guideNumber;

    private BigDecimal priceDiscount;

    @NotNull(message = "The Warehouse is required.")
    private Integer warehouseId;

    @NotBlank(message = "The Landshipment vehiclePlate is required.")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$",
            message = "The plate must be formatted as follows: 3 letters and 3 numbers (ABC123)")
    private String vehiclePlate;
}
