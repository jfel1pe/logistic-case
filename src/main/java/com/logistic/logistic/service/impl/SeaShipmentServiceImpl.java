package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.SeaShipmentDTO;
import com.logistic.logistic.entity.*;
import com.logistic.logistic.repository.*;
import com.logistic.logistic.service.SeaShipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeaShipmentServiceImpl implements SeaShipmentService {

    // ← única diferencia con LandShipment
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.03");
    private static final int DISCOUNT_THRESHOLD = 10;

    private final SeaShipmentRepository seaShipmentRepository;
    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final ProductTypeRepository productTypeRepository;
    private final PortRepository portRepository;

    public SeaShipmentServiceImpl(SeaShipmentRepository seaShipmentRepository,
                                  ShipmentRepository shipmentRepository,
                                  ClientRepository clientRepository,
                                  ProductTypeRepository productTypeRepository,
                                  PortRepository portRepository) {
        this.seaShipmentRepository = seaShipmentRepository;
        this.shipmentRepository = shipmentRepository;
        this.clientRepository = clientRepository;
        this.productTypeRepository = productTypeRepository;
        this.portRepository = portRepository;
    }

    @Override
    public SeaShipmentDTO createSeaShipment(SeaShipmentDTO dto) {

        // Validar número de guía único
        if (shipmentRepository.existsByGuideNumber(dto.getGuideNumber())) {
            throw new RuntimeException("An order with the tracking number already exists: "
                    + dto.getGuideNumber());
        }

        // Buscar entidades relacionadas
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("The client was not found whit id: "
                        + dto.getClientId()));

        ProductType productType = productTypeRepository.findById(dto.getProductTypeId())
                .orElseThrow(() -> new RuntimeException("The product was not found with id: "
                        + dto.getProductTypeId()));

        Port port = portRepository.findById(dto.getPortId())
                .orElseThrow(() -> new RuntimeException("The port was not found with id: "
                        + dto.getPortId()));

        // Calcular descuento 3% si quantity > 10
        BigDecimal priceDiscount = calculateDiscount(dto.getQuantity(), dto.getPrice());

        // Crear y guardar Shipment base primero
        Shipment shipment = new Shipment();
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(dto.getQuantity());
        shipment.setRegistryDate(dto.getRegistryDate());
        shipment.setDeliveryDate(dto.getDeliveryDate());
        shipment.setPrice(dto.getPrice());
        shipment.setGuideNumber(dto.getGuideNumber());
        shipment.setPriceDiscount(priceDiscount);
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Crear y guardar SeaShipment
        SeaShipment seaShipment = new SeaShipment();
        seaShipment.setShipment(savedShipment);
        seaShipment.setPort(port);
        seaShipment.setFleetNumber(dto.getFleetNumber());
        SeaShipment saved = seaShipmentRepository.save(seaShipment);

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeaShipmentDTO> getAllSeaShipments() {
        return seaShipmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SeaShipmentDTO getSeaShipmentById(Integer id) {
        SeaShipment seaShipment = seaShipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(GET)The SeaShipment was not found with id: " + id));
        return mapToDTO(seaShipment);
    }

    @Override
    public SeaShipmentDTO updateSeaShipment(Integer id, SeaShipmentDTO dto) {
        SeaShipment seaShipment = seaShipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(UPDATE)The SeaShipment was not found with id: " + id));

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("(UPDATE SEASHIPMENT)The client was not found whit id: "
                        + dto.getClientId()));

        ProductType productType = productTypeRepository.findById(dto.getProductTypeId())
                .orElseThrow(() -> new RuntimeException("(UPDATE LANDSHIPMENT)The product was not found with id: "
                        + dto.getProductTypeId()));

        Port port = portRepository.findById(dto.getPortId())
                .orElseThrow(() -> new RuntimeException("(UPDATE SEASHIPMENT)The port was not found with id: "
                        + dto.getPortId()));

        // Recalcular descuento
        BigDecimal priceDiscount = calculateDiscount(dto.getQuantity(), dto.getPrice());

        // Actualizar Shipment base
        Shipment shipment = seaShipment.getShipment();
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(dto.getQuantity());
        shipment.setRegistryDate(dto.getRegistryDate());
        shipment.setDeliveryDate(dto.getDeliveryDate());
        shipment.setPrice(dto.getPrice());
        shipment.setGuideNumber(dto.getGuideNumber());
        shipment.setPriceDiscount(priceDiscount);
        shipmentRepository.save(shipment);

        // Actualizar SeaShipment
        seaShipment.setPort(port);
        seaShipment.setFleetNumber(dto.getFleetNumber());
        SeaShipment updated = seaShipmentRepository.save(seaShipment);

        return mapToDTO(updated);
    }

    @Override
    public void deleteSeaShipment(Integer id) {
        SeaShipment seaShipment = seaShipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(DELETE SEASHIPMENT)The SeaShipment was not found with id: " + id));

        seaShipmentRepository.delete(seaShipment);
        shipmentRepository.delete(seaShipment.getShipment());
    }

    // ---- Lógica de descuento 3% ----
    private BigDecimal calculateDiscount(Integer quantity, BigDecimal price) {
        if (quantity > DISCOUNT_THRESHOLD) {
            BigDecimal discount = price.multiply(DISCOUNT_RATE);
            return price.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

    // ---- Mapeos ----
    private SeaShipmentDTO mapToDTO(SeaShipment seaShipment) {
        Shipment s = seaShipment.getShipment();
        return new SeaShipmentDTO(
                s.getId(),
                s.getClient().getId(),
                s.getProductType().getId(),
                s.getQuantity(),
                s.getRegistryDate(),
                s.getDeliveryDate(),
                s.getPrice(),
                s.getGuideNumber(),
                s.getPriceDiscount(),
                seaShipment.getPort().getId(),
                seaShipment.getFleetNumber()
        );
    }
}
