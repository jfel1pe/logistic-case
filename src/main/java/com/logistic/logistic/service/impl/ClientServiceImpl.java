package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.ClientDTO;
import com.logistic.logistic.entity.Client;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.ClientRepository;
import com.logistic.logistic.service.ClientService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = mapToEntity(clientDTO);
        Client saved = clientRepository.save(client);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(GET)The client was not found: " + id));
        return mapToDTO(client);
    }

    @Override
    public ClientDTO updateClient(Integer id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE)The client was not found: " + id));

        client.setDocumentId(clientDTO.getDocumentId());
        client.setName(clientDTO.getName());
        client.setDirection(clientDTO.getDirection());

        Client updated = clientRepository.save(client);
        return mapToDTO(updated);
    }

    @Override
    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("(DELETE)The client was not found: " + id);
        }
        clientRepository.deleteById(id);
    }

    // ---- Métodos privados de mapeo ----

    private ClientDTO mapToDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getDocumentId(),
                client.getName(),
                client.getDirection()
        );
    }

    private Client mapToEntity(ClientDTO dto) {
        return new Client(
                dto.getId(),
                dto.getDocumentId(),
                dto.getName(),
                dto.getDirection()
        );
    }
}
