package com.logistic.logistic.service;

import com.logistic.logistic.dto.SeaShipmentDTO;

import java.util.List;

public interface SeaShipmentService {

    SeaShipmentDTO createSeaShipment(SeaShipmentDTO seaShipmentDTO);
    List<SeaShipmentDTO> getAllSeaShipments();
    SeaShipmentDTO getSeaShipmentById(Integer id);
    SeaShipmentDTO updateSeaShipment(Integer id, SeaShipmentDTO seaShipmentDTO);
    void deleteSeaShipment(Integer id);
}
