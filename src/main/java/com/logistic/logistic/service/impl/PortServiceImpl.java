package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.PortDTO;
import com.logistic.logistic.entity.Port;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.PortRepository;
import com.logistic.logistic.service.PortService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortServiceImpl implements PortService {

    private final PortRepository portRepository;

    public PortServiceImpl(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    @Override
    public PortDTO createPort(PortDTO portDTO) {
        Port port = mapToEntity(portDTO);
        Port saved = portRepository.save(port);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortDTO> getAllPorts() {
        return portRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PortDTO getPortById(Integer id) {
        Port port = portRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(GET)The port was not found: " + id));
        return mapToDTO(port);
    }

    @Override
    public PortDTO updatePort(Integer id, PortDTO portDTO) {
        Port port = portRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE)The port was not found: " + id));

        port.setName(portDTO.getName());
        port.setCountry(portDTO.getCountry());
        port.setUbication(portDTO.getUbication());
        port.setInternational(portDTO.getInternational());

        Port updated = portRepository.save(port);
        return mapToDTO(updated);
    }

    @Override
    public void deletePort(Integer id) {
        if (!portRepository.existsById(id)) {
            throw new ResourceNotFoundException("(DELETE)The port was not found: " + id);
        }
        portRepository.deleteById(id);
    }

    private PortDTO mapToDTO(Port port) {
        return new PortDTO(
                port.getId(),
                port.getName(),
                port.getCountry(),
                port.getUbication(),
                port.getInternational()
        );
    }

    private Port mapToEntity(PortDTO dto) {
        return new Port(
                dto.getId(),
                dto.getName(),
                dto.getCountry(),
                dto.getUbication(),
                dto.getInternational()
        );
    }
}
