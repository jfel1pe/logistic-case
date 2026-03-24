package com.logistic.logistic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sea_shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeaShipment {

    @Id
    @Column(name = "shipment_id")
    private Integer shipmentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "port_id", nullable = false)
    private Port port;

    @Column(name = "fleet_number", nullable = false, length = 8)
    private String fleetNumber;
}
