package com.logistic.logistic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "port")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "country", nullable = false, length = 45)
    private String country;

    @Column(name = "ubication", nullable = false, length = 45)
    private String ubication;

    @Column(name = "international", nullable = false)
    private Boolean international;
}
