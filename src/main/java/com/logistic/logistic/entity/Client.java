package com.logistic.logistic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id", nullable = false, length = 45)
    private String documentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "direction", length = 100)
    private String direction;
}