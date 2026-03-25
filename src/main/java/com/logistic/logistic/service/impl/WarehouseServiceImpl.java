package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.WarehouseDTO;
import com.logistic.logistic.entity.Warehouse;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.WarehouseRepository;
import com.logistic.logistic.service.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = mapToEntity(warehouseDTO);
        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Integer id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(GET)The warehouse was not found: " + id));
        return mapToDTO(warehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE)The warehouse was not found: " + id));

        warehouse.setName(warehouseDTO.getName());
        warehouse.setCountry(warehouseDTO.getCountry());
        warehouse.setUbication(warehouseDTO.getUbication());

        Warehouse updated = warehouseRepository.save(warehouse);
        return mapToDTO(updated);
    }

    @Override
    public void deleteWarehouse(Integer id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("(DELETE)The warehouse was not found: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseDTO mapToDTO(Warehouse warehouse) {
        return new WarehouseDTO(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getCountry(),
                warehouse.getUbication()
        );
    }

    private Warehouse mapToEntity(WarehouseDTO dto) {
        return new Warehouse(
                dto.getId(),
                dto.getName(),
                dto.getCountry(),
                dto.getUbication()
        );
    }
}
