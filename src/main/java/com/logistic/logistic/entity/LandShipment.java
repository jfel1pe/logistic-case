package com.logistic.logistic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "land_shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandShipment {

    @Id
    @Column(name = "shipment_id")
    private Integer shipmentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "vehicle_plate", nullable = false, length = 6)
    private String vehiclePlate;
}
