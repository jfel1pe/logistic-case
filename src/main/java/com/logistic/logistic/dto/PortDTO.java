package com.logistic.logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortDTO {

    private Integer id;
    private String name;
    private String country;
    private String ubication;
    private Boolean international;
}
