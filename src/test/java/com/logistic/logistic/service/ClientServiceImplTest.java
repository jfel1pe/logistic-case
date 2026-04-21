package com.logistic.logistic.service;

import com.logistic.logistic.dto.ClientDTO;
import com.logistic.logistic.entity.Client;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.ClientRepository;
import com.logistic.logistic.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        // Datos de prueba reutilizables en todos los tests
        client = new Client(1, "123456789", "Juan Pérez", "Calle 123");
        clientDTO = new ClientDTO(null, "123456789", "Juan Pérez", "Calle 123");
    }

    // ─────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Crear cliente exitosamente")
    void createClient_Success() {
        // Arrange
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        ClientDTO result = clientService.createClient(clientDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Juan Pérez");
        assertThat(result.getDocumentId()).isEqualTo("123456789");
        assertThat(result.getDirection()).isEqualTo("Calle 123");

        // Verifica que save() fue llamado exactamente 1 vez
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    // ─────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener todos los clientes exitosamente")
    void getAllClients_Success() {
        // Arrange
        Client client2 = new Client(2, "987654321", "María López", "Carrera 99");
        when(clientRepository.findAll()).thenReturn(List.of(client, client2));

        // Act
        List<ClientDTO> result = clientService.getAllClients();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Juan Pérez");
        assertThat(result.get(1).getName()).isEqualTo("María López");

        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener lista vacía cuando no hay clientes")
    void getAllClients_EmptyList() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of());

        // Act
        List<ClientDTO> result = clientService.getAllClients();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Obtener cliente por ID exitosamente")
    void getClientById_Success() {
        // Arrange
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        // Act
        ClientDTO result = clientService.getClientById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Juan Pérez");

        verify(clientRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Lanza excepción cuando cliente no existe")
    void getClientById_NotFound() {
        // Arrange
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clientService.getClientById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(clientRepository, times(1)).findById(99);
    }

    // ─────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Actualizar cliente exitosamente")
    void updateClient_Success() {
        // Arrange
        ClientDTO updateDTO = new ClientDTO(null, "999888777",
                "Juan Actualizado", "Nueva Dirección");
        Client updatedClient = new Client(1, "999888777",
                "Juan Actualizado", "Nueva Dirección");

        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        // Act
        ClientDTO result = clientService.updateClient(1, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Juan Actualizado");
        assertThat(result.getDocumentId()).isEqualTo("999888777");
        assertThat(result.getDirection()).isEqualTo("Nueva Dirección");

        verify(clientRepository, times(1)).findById(1);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Lanza excepción al actualizar cliente que no existe")
    void updateClient_NotFound() {
        // Arrange
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clientService.updateClient(99, clientDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(clientRepository, never()).save(any(Client.class));
    }

    // ─────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Eliminar cliente exitosamente")
    void deleteClient_Success() {
        // Arrange
        when(clientRepository.existsById(1)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1);

        // Act
        clientService.deleteClient(1);

        // Assert
        verify(clientRepository, times(1)).existsById(1);
        verify(clientRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar cliente que no existe")
    void deleteClient_NotFound() {
        // Arrange
        when(clientRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> clientService.deleteClient(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(clientRepository, never()).deleteById(any());
    }
}
