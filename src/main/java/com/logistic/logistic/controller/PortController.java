package com.logistic.logistic.controller;

import com.logistic.logistic.dto.PortDTO;
import com.logistic.logistic.service.PortService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ports")
public class PortController {

    private final PortService portService;

    public PortController(PortService portService) {
        this.portService = portService;
    }

    @PostMapping
    public ResponseEntity<PortDTO> createPort(@RequestBody PortDTO portDTO) {
        PortDTO created = portService.createPort(portDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PortDTO>> getAllPorts() {
        List<PortDTO> ports = portService.getAllPorts();
        return ResponseEntity.ok(ports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortDTO> getPortById(@PathVariable Integer id) {
        PortDTO port = portService.getPortById(id);
        return ResponseEntity.ok(port);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortDTO> updatePort(
            @PathVariable Integer id,
            @RequestBody PortDTO portDTO) {
        PortDTO updated = portService.updatePort(id, portDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePort(@PathVariable Integer id) {
        portService.deletePort(id);
        return ResponseEntity.noContent().build();
    }
}
