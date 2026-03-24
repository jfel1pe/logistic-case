package com.logistic.logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO {

    private Integer id;
    private Integer clientId;
    private Integer productTypeId;
    private Integer quantity;
    private LocalDateTime registryDate;
    private LocalDateTime deliveryDate;
    private BigDecimal price;
    private String guideNumber;
    private BigDecimal priceDiscount;
}
