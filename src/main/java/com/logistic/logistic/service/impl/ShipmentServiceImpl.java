package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.ShipmentDTO;
import com.logistic.logistic.entity.Client;
import com.logistic.logistic.entity.ProductType;
import com.logistic.logistic.entity.Shipment;
import com.logistic.logistic.repository.ClientRepository;
import com.logistic.logistic.repository.ProductTypeRepository;
import com.logistic.logistic.repository.ShipmentRepository;
import com.logistic.logistic.service.ShipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final ProductTypeRepository productTypeRepository;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository,
                               ClientRepository clientRepository,
                               ProductTypeRepository productTypeRepository) {
        this.shipmentRepository = shipmentRepository;
        this.clientRepository = clientRepository;
        this.productTypeRepository = productTypeRepository;
    }

    @Override
    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {

        // Validar que el número de guía no exista
        if (shipmentRepository.existsByGuideNumber(shipmentDTO.getGuideNumber())) {
            throw new RuntimeException("An order with the tracking number already exists: "
                    + shipmentDTO.getGuideNumber());
        }

        // Buscar cliente
        Client client = clientRepository.findById(shipmentDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("The client was not found whit id: "
                        + shipmentDTO.getClientId()));

        // Buscar tipo de producto
        ProductType productType = productTypeRepository.findById(shipmentDTO.getProductTypeId())
                .orElseThrow(() -> new RuntimeException("The product was not found with id: "
                        + shipmentDTO.getProductTypeId()));

        Shipment shipment = mapToEntity(shipmentDTO, client, productType);
        Shipment saved = shipmentRepository.save(shipment);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentById(Integer id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(GET)The shipment was not found id: " + id));
        return mapToDTO(shipment);
    }

    @Override
    public ShipmentDTO updateShipment(Integer id, ShipmentDTO shipmentDTO) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(UPDATE)The shipment was not found id: " + id));

        Client client = clientRepository.findById(shipmentDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("(UPDATE SHIPMENT)The client was not found whit id: "
                        + shipmentDTO.getClientId()));

        ProductType productType = productTypeRepository.findById(shipmentDTO.getProductTypeId())
                .orElseThrow(() -> new RuntimeException("(UPDATE SHIPMENT)The product was not found with id: "
                        + shipmentDTO.getProductTypeId()));

        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(shipmentDTO.getQuantity());
        shipment.setRegistryDate(shipmentDTO.getRegistryDate());
        shipment.setDeliveryDate(shipmentDTO.getDeliveryDate());
        shipment.setPrice(shipmentDTO.getPrice());
        shipment.setGuideNumber(shipmentDTO.getGuideNumber());
        shipment.setPriceDiscount(shipmentDTO.getPriceDiscount());

        Shipment updated = shipmentRepository.save(shipment);
        return mapToDTO(updated);
    }

    @Override
    public void deleteShipment(Integer id) {
        if (!shipmentRepository.existsById(id)) {
            throw new RuntimeException("(DELETE) The shipment was not found id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    private ShipmentDTO mapToDTO(Shipment shipment) {
        return new ShipmentDTO(
                shipment.getId(),
                shipment.getClient().getId(),
                shipment.getProductType().getId(),
                shipment.getQuantity(),
                shipment.getRegistryDate(),
                shipment.getDeliveryDate(),
                shipment.getPrice(),
                shipment.getGuideNumber(),
                shipment.getPriceDiscount()
        );
    }

    private Shipment mapToEntity(ShipmentDTO dto, Client client, ProductType productType) {
        return new Shipment(
                dto.getId(),
                client,
                productType,
                dto.getQuantity(),
                dto.getRegistryDate(),
                dto.getDeliveryDate(),
                dto.getPrice(),
                dto.getGuideNumber(),
                dto.getPriceDiscount()
        );
    }
}
