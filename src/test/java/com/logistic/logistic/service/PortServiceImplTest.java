package com.logistic.logistic.service;

import com.logistic.logistic.dto.PortDTO;
import com.logistic.logistic.entity.Port;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.PortRepository;
import com.logistic.logistic.service.impl.PortServiceImpl;
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
class PortServiceImplTest {

    @Mock
    private PortRepository portRepository;

    @InjectMocks
    private PortServiceImpl portService;

    private Port port;
    private PortDTO portDTO;

    @BeforeEach
    void setUp() {
        port = new Port(1, "Puerto de Cartagena", "Colombia", "Cartagena", false);
        portDTO = new PortDTO(null, "Puerto de Cartagena", "Colombia", "Cartagena", false);
    }

    @Test
    @DisplayName("Crear puerto exitosamente")
    void createPort_Success() {
        when(portRepository.save(any(Port.class))).thenReturn(port);

        PortDTO result = portService.createPort(portDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Puerto de Cartagena");
        assertThat(result.getInternational()).isFalse();
        verify(portRepository, times(1)).save(any(Port.class));
    }

    @Test
    @DisplayName("Crear puerto internacional exitosamente")
    void createPort_International_Success() {
        Port internationalPort = new Port(2, "Puerto de Miami",
                "Estados Unidos", "Miami", true);
        PortDTO internationalDTO = new PortDTO(null, "Puerto de Miami",
                "Estados Unidos", "Miami", true);

        when(portRepository.save(any(Port.class))).thenReturn(internationalPort);

        PortDTO result = portService.createPort(internationalDTO);

        assertThat(result).isNotNull();
        assertThat(result.getInternational()).isTrue();
    }

    @Test
    @DisplayName("Obtener todos los puertos")
    void getAllPorts_Success() {
        Port port2 = new Port(2, "Puerto de Barranquilla",
                "Colombia", "Barranquilla", false);
        when(portRepository.findAll()).thenReturn(List.of(port, port2));

        List<PortDTO> result = portService.getAllPorts();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Obtener puerto por ID exitosamente")
    void getPortById_Success() {
        when(portRepository.findById(1)).thenReturn(Optional.of(port));

        PortDTO result = portService.getPortById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Puerto de Cartagena");
        assertThat(result.getInternational()).isFalse();
    }

    @Test
    @DisplayName("Lanza excepción cuando puerto no existe")
    void getPortById_NotFound() {
        when(portRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portService.getPortById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Actualizar puerto exitosamente")
    void updatePort_Success() {
        PortDTO updateDTO = new PortDTO(null, "Puerto de Buenaventura",
                "Colombia", "Buenaventura", false);
        Port updated = new Port(1, "Puerto de Buenaventura",
                "Colombia", "Buenaventura", false);

        when(portRepository.findById(1)).thenReturn(Optional.of(port));
        when(portRepository.save(any(Port.class))).thenReturn(updated);

        PortDTO result = portService.updatePort(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Puerto de Buenaventura");
    }

    @Test
    @DisplayName("Lanza excepción al actualizar puerto que no existe")
    void updatePort_NotFound() {
        when(portRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portService.updatePort(99, portDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(portRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar puerto exitosamente")
    void deletePort_Success() {
        when(portRepository.existsById(1)).thenReturn(true);
        doNothing().when(portRepository).deleteById(1);

        portService.deletePort(1);

        verify(portRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar puerto que no existe")
    void deletePort_NotFound() {
        when(portRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> portService.deletePort(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(portRepository, never()).deleteById(any());
    }
}
