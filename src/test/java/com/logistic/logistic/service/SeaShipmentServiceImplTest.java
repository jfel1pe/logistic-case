package com.logistic.logistic.service;

import com.logistic.logistic.dto.SeaShipmentDTO;
import com.logistic.logistic.entity.*;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.*;
import com.logistic.logistic.service.impl.SeaShipmentServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SeaShipmentServiceImplTest {

    @Mock
    private SeaShipmentRepository seaShipmentRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private PortRepository portRepository;

    @Mock
    private GuideNumberGenerator guideNumberGenerator;

    @InjectMocks
    private SeaShipmentServiceImpl seaShipmentService;

    private Client client;
    private ProductType productType;
    private Port port;
    private Shipment shipment;
    private SeaShipment seaShipment;
    private SeaShipmentDTO seaShipmentDTO;

    @BeforeEach
    void setUp() {
        client = new Client(1, "123456789", "Juan Pérez", "Calle 123");
        productType = new ProductType(1, "Electrónica", "Productos electrónicos");
        port = new Port(1, "Puerto de Cartagena", "Colombia", "Cartagena", false);

        shipment = new Shipment();
        shipment.setId(1);
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(5);
        shipment.setRegistryDate(LocalDateTime.now());
        shipment.setDeliveryDate(LocalDateTime.now().plusDays(10));
        shipment.setPrice(new BigDecimal("200000.00"));
        shipment.setGuideNumber("SEA1234567");
        shipment.setPriceDiscount(new BigDecimal("200000.00"));

        seaShipment = new SeaShipment();
        seaShipment.setShipment(shipment);
        seaShipment.setPort(port);
        seaShipment.setFleetNumber("ABC1234X");

        seaShipmentDTO = new SeaShipmentDTO(
                null, 1, 1, 5,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                new BigDecimal("200000.00"),
                null, null,
                1, "ABC1234X"
        );
    }

    // ─────────────────────────────────────────
    // CREATE — Lógica de descuentos
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("Pruebas de descuento marítimo")
    class DiscountTests {

        @Test
        @DisplayName("No aplica descuento cuando quantity <= 10")
        void createSeaShipment_NoDiscount_WhenQuantityLessOrEqualTen() {
            // Arrange
            seaShipmentDTO.setQuantity(5);
            seaShipmentDTO.setPrice(new BigDecimal("200000.00"));

            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(portRepository.findById(1)).thenReturn(Optional.of(port));
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
            when(seaShipmentRepository.save(any(SeaShipment.class))).thenReturn(seaShipment);

            // Act
            SeaShipmentDTO result = seaShipmentService.createSeaShipment(seaShipmentDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPriceDiscount())
                    .isEqualByComparingTo(new BigDecimal("200000.00"));
        }

        @Test
        @DisplayName("Aplica descuento 3% cuando quantity > 10")
        void createSeaShipment_AppliesDiscount_WhenQuantityGreaterThanTen() {
            // Arrange
            seaShipmentDTO.setQuantity(15);
            seaShipmentDTO.setPrice(new BigDecimal("200000.00"));

            Shipment shipmentWithDiscount = new Shipment();
            shipmentWithDiscount.setId(1);
            shipmentWithDiscount.setClient(client);
            shipmentWithDiscount.setProductType(productType);
            shipmentWithDiscount.setQuantity(15);
            shipmentWithDiscount.setRegistryDate(LocalDateTime.now());
            shipmentWithDiscount.setDeliveryDate(LocalDateTime.now().plusDays(10));
            shipmentWithDiscount.setPrice(new BigDecimal("200000.00"));
            shipmentWithDiscount.setGuideNumber("SEA1234567");
            shipmentWithDiscount.setPriceDiscount(new BigDecimal("194000.00"));

            SeaShipment seaShipmentWithDiscount = new SeaShipment();
            seaShipmentWithDiscount.setShipment(shipmentWithDiscount);
            seaShipmentWithDiscount.setPort(port);
            seaShipmentWithDiscount.setFleetNumber("ABC1234X");

            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(portRepository.findById(1)).thenReturn(Optional.of(port));
            when(shipmentRepository.save(any(Shipment.class)))
                    .thenReturn(shipmentWithDiscount);
            when(seaShipmentRepository.save(any(SeaShipment.class)))
                    .thenReturn(seaShipmentWithDiscount);

            // Act
            SeaShipmentDTO result = seaShipmentService.createSeaShipment(seaShipmentDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPriceDiscount())
                    .isEqualByComparingTo(new BigDecimal("194000.00"));
        }

        @ParameterizedTest
        @DisplayName("Verifica descuento 3% con diferentes precios")
        @CsvSource({
                "200000, 11, 194000.00",
                "100000, 15, 97000.00",
                "500000, 20, 485000.00",
                "50000,  10, 50000.00"   // cantidad = 10 → sin descuento
        })
        void createSeaShipment_DiscountCalculation(
                String price, int quantity, String expectedDiscount) {

            // Arrange
            seaShipmentDTO.setQuantity(quantity);
            seaShipmentDTO.setPrice(new BigDecimal(price));

            Shipment shipmentParam = new Shipment();
            shipmentParam.setId(1);
            shipmentParam.setClient(client);
            shipmentParam.setProductType(productType);
            shipmentParam.setQuantity(quantity);
            shipmentParam.setRegistryDate(LocalDateTime.now());
            shipmentParam.setDeliveryDate(LocalDateTime.now().plusDays(10));
            shipmentParam.setPrice(new BigDecimal(price));
            shipmentParam.setGuideNumber("SEA1234567");
            shipmentParam.setPriceDiscount(new BigDecimal(expectedDiscount));

            SeaShipment seaShipmentParam = new SeaShipment();
            seaShipmentParam.setShipment(shipmentParam);
            seaShipmentParam.setPort(port);
            seaShipmentParam.setFleetNumber("ABC1234X");

            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(portRepository.findById(1)).thenReturn(Optional.of(port));
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipmentParam);
            when(seaShipmentRepository.save(any(SeaShipment.class)))
                    .thenReturn(seaShipmentParam);

            // Act
            SeaShipmentDTO result = seaShipmentService.createSeaShipment(seaShipmentDTO);

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
        void createSeaShipment_ThrowsException_WhenClientNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(99)).thenReturn(Optional.empty());
            seaShipmentDTO.setClientId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    seaShipmentService.createSeaShipment(seaShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
            verify(seaShipmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción cuando tipo de producto no existe")
        void createSeaShipment_ThrowsException_WhenProductTypeNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(99)).thenReturn(Optional.empty());
            seaShipmentDTO.setProductTypeId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    seaShipmentService.createSeaShipment(seaShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción cuando puerto no existe")
        void createSeaShipment_ThrowsException_WhenPortNotFound() {
            // Arrange
            when(guideNumberGenerator.generate()).thenReturn("SEA1234567");
            when(clientRepository.findById(1)).thenReturn(Optional.of(client));
            when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
            when(portRepository.findById(99)).thenReturn(Optional.empty());
            seaShipmentDTO.setPortId(99);

            // Act & Assert
            assertThatThrownBy(() ->
                    seaShipmentService.createSeaShipment(seaShipmentDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(shipmentRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener todos los envíos marítimos")
    void getAllSeaShipments_Success() {
        // Arrange
        when(seaShipmentRepository.findAll()).thenReturn(List.of(seaShipment));

        // Act
        List<SeaShipmentDTO> result = seaShipmentService.getAllSeaShipments();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFleetNumber()).isEqualTo("ABC1234X");

        verify(seaShipmentRepository, times(1)).findAll();
    }

    // ─────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener envío marítimo por ID exitosamente")
    void getSeaShipmentById_Success() {
        // Arrange
        when(seaShipmentRepository.findById(1))
                .thenReturn(Optional.of(seaShipment));

        // Act
        SeaShipmentDTO result = seaShipmentService.getSeaShipmentById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFleetNumber()).isEqualTo("ABC1234X");
        assertThat(result.getPortId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Lanza excepción cuando envío marítimo no existe")
    void getSeaShipmentById_NotFound() {
        // Arrange
        when(seaShipmentRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                seaShipmentService.getSeaShipmentById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Eliminar envío marítimo exitosamente")
    void deleteSeaShipment_Success() {
        // Arrange
        when(seaShipmentRepository.findById(1))
                .thenReturn(Optional.of(seaShipment));
        doNothing().when(seaShipmentRepository).delete(any(SeaShipment.class));
        doNothing().when(shipmentRepository).delete(any(Shipment.class));

        // Act
        seaShipmentService.deleteSeaShipment(1);

        // Assert
        verify(seaShipmentRepository, times(1)).delete(any(SeaShipment.class));
        verify(shipmentRepository, times(1)).delete(any(Shipment.class));
    }

    @Test
    @DisplayName("Lanza excepción al eliminar envío marítimo que no existe")
    void deleteSeaShipment_NotFound() {
        // Arrange
        when(seaShipmentRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                seaShipmentService.deleteSeaShipment(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(seaShipmentRepository, never()).delete(any());
        verify(shipmentRepository, never()).delete(any());
    }
}
