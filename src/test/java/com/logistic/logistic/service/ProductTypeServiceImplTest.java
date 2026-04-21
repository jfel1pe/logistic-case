package com.logistic.logistic.service;

import com.logistic.logistic.dto.ProductTypeDTO;
import com.logistic.logistic.entity.ProductType;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.ProductTypeRepository;
import com.logistic.logistic.service.impl.ProductTypeServiceImpl;
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
class ProductTypeServiceImplTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @InjectMocks
    private ProductTypeServiceImpl productTypeService;

    private ProductType productType;
    private ProductTypeDTO productTypeDTO;

    @BeforeEach
    void setUp() {
        productType = new ProductType(1, "Electrónica", "Productos electrónicos");
        productTypeDTO = new ProductTypeDTO(null, "Electrónica", "Productos electrónicos");
    }

    @Test
    @DisplayName("Crear tipo de producto exitosamente")
    void createProductType_Success() {
        when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);

        ProductTypeDTO result = productTypeService.createProductType(productTypeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electrónica");
        assertThat(result.getDescription()).isEqualTo("Productos electrónicos");
        verify(productTypeRepository, times(1)).save(any(ProductType.class));
    }

    @Test
    @DisplayName("Obtener todos los tipos de producto")
    void getAllProductTypes_Success() {
        ProductType productType2 = new ProductType(2, "Ropa", "Prendas de vestir");
        when(productTypeRepository.findAll()).thenReturn(List.of(productType, productType2));

        List<ProductTypeDTO> result = productTypeService.getAllProductTypes();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Electrónica");
        assertThat(result.get(1).getName()).isEqualTo("Ropa");
    }

    @Test
    @DisplayName("Obtener tipo de producto por ID exitosamente")
    void getProductTypeById_Success() {
        when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));

        ProductTypeDTO result = productTypeService.getProductTypeById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Electrónica");
    }

    @Test
    @DisplayName("Lanza excepción cuando tipo de producto no existe")
    void getProductTypeById_NotFound() {
        when(productTypeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productTypeService.getProductTypeById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Actualizar tipo de producto exitosamente")
    void updateProductType_Success() {
        ProductTypeDTO updateDTO = new ProductTypeDTO(null, "Ropa", "Prendas de vestir");
        ProductType updated = new ProductType(1, "Ropa", "Prendas de vestir");

        when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
        when(productTypeRepository.save(any(ProductType.class))).thenReturn(updated);

        ProductTypeDTO result = productTypeService.updateProductType(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ropa");
        verify(productTypeRepository, times(1)).save(any(ProductType.class));
    }

    @Test
    @DisplayName("Lanza excepción al actualizar tipo de producto que no existe")
    void updateProductType_NotFound() {
        when(productTypeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productTypeService.updateProductType(99, productTypeDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar tipo de producto exitosamente")
    void deleteProductType_Success() {
        when(productTypeRepository.existsById(1)).thenReturn(true);
        doNothing().when(productTypeRepository).deleteById(1);

        productTypeService.deleteProductType(1);

        verify(productTypeRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar tipo de producto que no existe")
    void deleteProductType_NotFound() {
        when(productTypeRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> productTypeService.deleteProductType(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productTypeRepository, never()).deleteById(any());
    }
}
