package com.logistic.logistic.service;

import com.logistic.logistic.dto.LandShipmentDTO;
import com.logistic.logistic.entity.*;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.*;
import com.logistic.logistic.service.impl.LandShipmentServiceImpl;
import com.logistic.logistic.util.GuideNumberGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LandShipmentServiceImplTest {

    @Mock
    private LandShipmentRepository landShipmentRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private GuideNumberGenerator guideNumberGenerator;

    @InjectMocks
    private LandShipmentServiceImpl landShipmentService;

    private Client client;
    private ProductType productType;
    private Warehouse warehouse;
    private Shipment shipment;
    private LandShipment landShipment;
    private LandShipmentDTO landShipmentDTO;

    @BeforeEach
    void setUp() {
        // Datos base reutilizables
        client = new Client(1, "123456789", "Juan Pérez", "Calle 123");
        productType = new ProductType(1, "Electrónica", "Productos electrónicos");
        warehouse = new Warehouse(1, "Bodega Central", "Colombia", "Bogotá");

        shipment = new Shipment();
        shipment.setId(1);
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(5);
        shipment.setRegistryDate(LocalDateTime.now());
        shipment.setDeliveryDate(LocalDateTime.now().plusDays(5));
        shipment.setPrice(new BigDecimal("100000.00"));
        shipment.setGuideNumber("ABC1234567");
        shipment.setPriceDiscount(new BigDecimal("100000.00"));

        landShipment = new LandShipment();
        landShipment.setShipment(shipment);
        landShipment.setWarehouse(warehouse);
        landShipment.setVehiclePlate("ABC123");

        landShipmentDTO = new LandShipmentDTO(
                null, 1, 1, 5,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                new BigDecimal("100000.00"),
                null,
                null,
                1, "ABC123"
        );
    }

    // ─────────────────────────────────────────
    // CREATE — Lógica de descuentos
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("Pruebas de descuento")
    class DiscountTests {

        @Test
        @DisplayName("No aplica descuento cuando quantity <= 10")
        void createLandShipment_NoDiscount_WhenQuantityLessOrEqualTen() {
            // Arrange
            landShipmentDTO.setQuantity(5); // <= 10 → sin descuento
            landShipmentDTO.setPrice(new BigDecimal("100000.00"));

            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
            when(landShipmentRepository.save(any(LandShipment.class))).thenReturn(landShipment);

            // Act
            LandShipmentDTO result = landShipmentService.createLandShipment(landShipmentDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPriceDiscount())
                    .isEqualByComparingTo(new BigDecimal("100000.00"));
        }

        @Test
        @DisplayName("Aplica descuento 5% cuando quantity > 10")
        void createLandShipment_AppliesDiscount_WhenQuantityGreaterThanTen() {
            // Arrange
            landShipmentDTO.setQuantity(15); // > 10 → descuento 5%
            landShipmentDTO.setPrice(new BigDecimal("100000.00"));

            // Shipment con descuento aplicado
            Shipment shipmentWithDiscount = new Shipment();
            shipmentWithDiscount.setId(1);
            shipmentWithDiscount.setClient(client);
            shipmentWithDiscount.setProductType(productType);
            shipmentWithDiscount.setQuantity(15);
            shipmentWithDiscount.setRegistryDate(LocalDateTime.now());
            shipmentWithDiscount.setDeliveryDate(LocalDateTime.now().plusDays(5));
            shipmentWithDiscount.setPrice(new BigDecimal("100000.00"));
            shipmentWithDiscount.setGuideNumber("ABC1234567");
            shipmentWithDiscount.setPriceDiscount(new BigDecimal("95000.00"));

            LandShipment landShipmentWithDiscount = new LandShipment();
            landShipmentWithDiscount.setShipment(shipmentWithDiscount);
            landShipmentWithDiscount.setWarehouse(warehouse);
            landShipmentWithDiscount.setVehiclePlate("ABC123");

            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));
            when(shipmentRepository.save(any(Shipment.class)))
                    .thenReturn(shipmentWithDiscount);
            when(landShipmentRepository.save(any(LandShipment.class)))
                    .thenReturn(landShipmentWithDiscount);

            // Act
            LandShipmentDTO result = landShipmentService.createLandShipment(landShipmentDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPriceDiscount())
                    .isEqualByComparingTo(new BigDecimal("95000.00"));
        }

        @ParameterizedTest
        @DisplayName("Verifica descuento 5% con diferentes precios")
        @CsvSource({
                "100000, 11, 95000.00",   // precio, cantidad, precio esperado con descuento
                "200000, 15, 190000.00",
                "500000, 20, 475000.00",
                "50000,  10, 50000.00"    // cantidad = 10 → sin descuento
        })
        void createLandShipment_DiscountCalculation(
                String price, int quantity, String expectedDiscount) {

            // Arrange
            landShipmentDTO.setQuantity(quantity);
            landShipmentDTO.setPrice(new BigDecimal(price));

            Shipment shipmentParam = new Shipment();
            shipmentParam.setId(1);
            shipmentParam.setClient(client);
            shipmentParam.setProductType(productType);
            shipmentParam.setQuantity(quantity);
            shipmentParam.setRegistryDate(LocalDateTime.now());
            shipmentParam.setDeliveryDate(LocalDateTime.now().plusDays(5));
            shipmentParam.setPrice(new BigDecimal(price));
            shipmentParam.setGuideNumber("ABC1234567");
            shipmentParam.setPriceDiscount(new BigDecimal(expectedDiscount));

            LandShipment landShipmentParam = new LandShipment();
            landShipmentParam.setShipment(shipmentParam);
            landShipmentParam.setWarehouse(warehouse);
            landShipmentParam.setVehiclePlate("ABC123");

            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipmentParam);
            when(landShipmentRepository.save(any(LandShipment.class)))
                    .thenReturn(landShipmentParam);

            // Act
            LandShipmentDTO result = landShipmentService.createLandShipment(landShipmentDTO);

            // Assert
            assertThat(result.getPriceDiscount())
                    .isEqualByComparingTo(new BigDecimal(expectedDiscount));
        }
    }

    // ─────────────────────────────────────────
    // CREATE — Validaciones
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("Pruebas de validación al crear")
    class CreateValidationTests {

        @Test
        @DisplayName("Lanza excepción cuando cliente no existe")
        void createLandShipment_ThrowsException_WhenClientNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(99)).thenReturn(Optional.empty());
            landShipmentDTO.setClientId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    landShipmentService.createLandShipment(landShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
            verify(landShipmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción cuando tipo de producto no existe")
        void createLandShipment_ThrowsException_WhenProductTypeNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(99)).thenReturn(Optional.empty());
            landShipmentDTO.setProductTypeId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    landShipmentService.createLandShipment(landShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción cuando bodega no existe")
        void createLandShipment_ThrowsException_WhenWarehouseNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("ABC1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(warehouseRepository.findById(99)).thenReturn(Optional.empty());
            landShipmentDTO.setWarehouseId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    landShipmentService.createLandShipment(landShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener todos los envíos terrestres")
    void getAllLandShipments_Success() {
        // Arrange
        when(landShipmentRepository.findAll()).thenReturn(List.of(landShipment));

        // Act
        List<LandShipmentDTO> result = landShipmentService.getAllLandShipments();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVehiclePlate()).isEqualTo("ABC123");

        verify(landShipmentRepository, times(1)).findAll();
    }

    // ─────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener envío terrestre por ID exitosamente")
    void getLandShipmentById_Success() {
        // Arrange
        when(landShipmentRepository.findById(1))
                .thenReturn(Optional.of(landShipment));

        // Act
        LandShipmentDTO result = landShipmentService.getLandShipmentById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getVehiclePlate()).isEqualTo("ABC123");
        assertThat(result.getWarehouseId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Lanza excepción cuando envío terrestre no existe")
    void getLandShipmentById_NotFound() {
        // Arrange
        when(landShipmentRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                landShipmentService.getLandShipmentById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Eliminar envío terrestre exitosamente")
    void deleteLandShipment_Success() {
        // Arrange
        when(landShipmentRepository.findById(1))
                .thenReturn(Optional.of(landShipment));
        doNothing().when(landShipmentRepository).delete(any(LandShipment.class));
        doNothing().when(shipmentRepository).delete(any(Shipment.class));

        // Act
        landShipmentService.deleteLandShipment(1);

        // Assert
        verify(landShipmentRepository, times(1)).delete(any(LandShipment.class));
        verify(shipmentRepository, times(1)).delete(any(Shipment.class));
    }

    @Test
    @DisplayName("Lanza excepción al eliminar envío terrestre que no existe")
    void deleteLandShipment_NotFound() {
        // Arrange
        when(landShipmentRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                landShipmentService.deleteLandShipment(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(landShipmentRepository, never()).delete(any());
        verify(shipmentRepository, never()).delete(any());
    }
}
