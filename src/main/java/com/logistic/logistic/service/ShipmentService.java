package com.logistic.logistic.service;

import com.logistic.logistic.dto.ShipmentDTO;

import java.util.List;

public interface ShipmentService {

    ShipmentDTO createShipment(ShipmentDTO shipmentDTO);
    List<ShipmentDTO> getAllShipments();
    ShipmentDTO getShipmentById(Integer id);
    ShipmentDTO updateShipment(Integer id, ShipmentDTO shipmentDTO);
    void deleteShipment(Integer id);
}
