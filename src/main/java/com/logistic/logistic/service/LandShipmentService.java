package com.logistic.logistic.service;

import com.logistic.logistic.dto.LandShipmentDTO;

import java.util.List;

public interface LandShipmentService {

    LandShipmentDTO createLandShipment(LandShipmentDTO landShipmentDTO);
    List<LandShipmentDTO> getAllLandShipments();
    LandShipmentDTO getLandShipmentById(Integer id);
    LandShipmentDTO updateLandShipment(Integer id, LandShipmentDTO landShipmentDTO);
    void deleteLandShipment(Integer id);
}
