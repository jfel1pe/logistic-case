package com.logistic.logistic.service;

import com.logistic.logistic.dto.WarehouseDTO;
import java.util.List;

public interface WarehouseService {

    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);
    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO getWarehouseById(Integer id);
    WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO);
    void deleteWarehouse(Integer id);
}
