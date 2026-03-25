package com.logistic.logistic.controller;

import com.logistic.logistic.dto.LandShipmentDTO;
import com.logistic.logistic.service.LandShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/land-shipments")
public class LandShipmentController {

    private final LandShipmentService landShipmentService;

    public LandShipmentController(LandShipmentService landShipmentService) {
        this.landShipmentService = landShipmentService;
    }

    @PostMapping
    public ResponseEntity<LandShipmentDTO> createLandShipment(
            @Valid @RequestBody LandShipmentDTO landShipmentDTO) {
        LandShipmentDTO created = landShipmentService.createLandShipment(landShipmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<LandShipmentDTO>> getAllLandShipments() {
        List<LandShipmentDTO> landShipments = landShipmentService.getAllLandShipments();
        return ResponseEntity.ok(landShipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandShipmentDTO> getLandShipmentById(@PathVariable Integer id) {
        LandShipmentDTO landShipment = landShipmentService.getLandShipmentById(id);
        return ResponseEntity.ok(landShipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandShipmentDTO> updateLandShipment(
            @PathVariable Integer id,
            @Valid @RequestBody LandShipmentDTO landShipmentDTO) {
        LandShipmentDTO updated = landShipmentService.updateLandShipment(id, landShipmentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLandShipment(@PathVariable Integer id) {
        landShipmentService.deleteLandShipment(id);
        return ResponseEntity.noContent().build();
    }
}
