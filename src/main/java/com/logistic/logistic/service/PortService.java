package com.logistic.logistic.service;

import com.logistic.logistic.dto.PortDTO;

import java.util.List;

public interface PortService {

    PortDTO createPort(PortDTO portDTO);
    List<PortDTO> getAllPorts();
    PortDTO getPortById(Integer id);
    PortDTO updatePort(Integer id, PortDTO portDTO);
    void deletePort(Integer id);
}
