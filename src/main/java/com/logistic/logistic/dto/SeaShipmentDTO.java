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
public class SeaShipmentDTO {

    // Datos del Shipment base
    private Integer id;
    @NotNull(message = "The client is required.")
    private Integer clientId;

    @NotNull(message = "The product is required.")
    private Integer productTypeId;

    @NotNull(message = "The SeaShipment quantity is required.")
    @Min(value = 1, message = "The SeaShipment quantity must be greater than 0")
    private Integer quantity;

    private LocalDateTime registryDate; // se genera automáticamente

    @NotNull(message = "The SeaShipment deliveryDate is required.")
    private LocalDateTime deliveryDate;

    @NotNull(message = "The SeaShipment price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "The SeaShipment price must be greater than 0")
    private BigDecimal price;

    // se genera automáticamente
    private String guideNumber;

    private BigDecimal priceDiscount;

    @NotNull(message = "The Port is required.")
    private Integer portId;

    @NotBlank(message = "The SeaShipment fleetNumber is required.")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{4}[A-Z]{1}$",
            message = "The plate must be formatted as follows: 3 letters, 4 numbers y 1 letter (ABC1234X)")
    private String fleetNumber;
}
