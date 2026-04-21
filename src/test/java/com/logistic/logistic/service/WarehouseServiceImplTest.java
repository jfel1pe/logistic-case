package com.logistic.logistic.service;

import com.logistic.logistic.dto.WarehouseDTO;
import com.logistic.logistic.entity.Warehouse;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.WarehouseRepository;
import com.logistic.logistic.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse(1, "Bodega Central", "Colombia", "Bogotá");
        warehouseDTO = new WarehouseDTO(null, "Bodega Central", "Colombia", "Bogotá");
    }

    @Test
    @DisplayName("Crear bodega exitosamente")
    void createWarehouse_Success() {
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bodega Central");
        assertThat(result.getCountry()).isEqualTo("Colombia");
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Obtener todas las bodegas")
    void getAllWarehouses_Success() {
        Warehouse warehouse2 = new Warehouse(2, "Bodega Norte", "Colombia", "Medellín");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse, warehouse2));

        List<WarehouseDTO> result = warehouseService.getAllWarehouses();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Obtener bodega por ID exitosamente")
    void getWarehouseById_Success() {
        when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));

        WarehouseDTO result = warehouseService.getWarehouseById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bodega Central");
    }

    @Test
    @DisplayName("Lanza excepción cuando bodega no existe")
    void getWarehouseById_NotFound() {
        when(warehouseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getWarehouseById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Actualizar bodega exitosamente")
    void updateWarehouse_Success() {
        WarehouseDTO updateDTO = new WarehouseDTO(null, "Bodega Sur", "Colombia", "Cali");
        Warehouse updated = new Warehouse(1, "Bodega Sur", "Colombia", "Cali");

        when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updated);

        WarehouseDTO result = warehouseService.updateWarehouse(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bodega Sur");
        assertThat(result.getUbication()).isEqualTo("Cali");
    }

    @Test
    @DisplayName("Lanza excepción al actualizar bodega que no existe")
    void updateWarehouse_NotFound() {
        when(warehouseRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.updateWarehouse(99, warehouseDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar bodega exitosamente")
    void deleteWarehouse_Success() {
        when(warehouseRepository.existsById(1)).thenReturn(true);
        doNothing().when(warehouseRepository).deleteById(1);

        warehouseService.deleteWarehouse(1);

        verify(warehouseRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar bodega que no existe")
    void deleteWarehouse_NotFound() {
        when(warehouseRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> warehouseService.deleteWarehouse(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(warehouseRepository, never()).deleteById(any());
    }
}
