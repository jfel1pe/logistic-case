package com.logistic.logistic.service;

import com.logistic.logistic.dto.ClientDTO;
import java.util.List;

public interface ClientService {

    ClientDTO createClient(ClientDTO clientDTO);
    List<ClientDTO> getAllClients();
    ClientDTO getClientById(Integer id);
    ClientDTO updateClient(Integer id, ClientDTO clientDTO);
    void deleteClient(Integer id);
}
