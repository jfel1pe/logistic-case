package com.logistic.logistic.controller;

import com.logistic.logistic.dto.SeaShipmentDTO;
import com.logistic.logistic.service.SeaShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sea-shipments")
public class SeaShipmentController {

    private final SeaShipmentService seaShipmentService;

    public SeaShipmentController(SeaShipmentService seaShipmentService) {
        this.seaShipmentService = seaShipmentService;
    }

    @PostMapping
    public ResponseEntity<SeaShipmentDTO> createSeaShipment(
            @RequestBody SeaShipmentDTO seaShipmentDTO) {
        SeaShipmentDTO created = seaShipmentService.createSeaShipment(seaShipmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SeaShipmentDTO>> getAllSeaShipments() {
        List<SeaShipmentDTO> seaShipments = seaShipmentService.getAllSeaShipments();
        return ResponseEntity.ok(seaShipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeaShipmentDTO> getSeaShipmentById(@PathVariable Integer id) {
        SeaShipmentDTO seaShipment = seaShipmentService.getSeaShipmentById(id);
        return ResponseEntity.ok(seaShipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeaShipmentDTO> updateSeaShipment(
            @PathVariable Integer id,
            @RequestBody SeaShipmentDTO seaShipmentDTO) {
        SeaShipmentDTO updated = seaShipmentService.updateSeaShipment(id, seaShipmentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeaShipment(@PathVariable Integer id) {
        seaShipmentService.deleteSeaShipment(id);
        return ResponseEntity.noContent().build();
    }
}