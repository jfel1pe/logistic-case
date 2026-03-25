package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.LandShipmentDTO;
import com.logistic.logistic.entity.*;
import com.logistic.logistic.exception.DuplicateResourceException;
import com.logistic.logistic.exception.ResourceNotFoundException;
import com.logistic.logistic.repository.*;
import com.logistic.logistic.service.LandShipmentService;
import com.logistic.logistic.util.GuideNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LandShipmentServiceImpl implements LandShipmentService {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");
    private static final int DISCOUNT_THRESHOLD = 10;

    private final LandShipmentRepository landShipmentRepository;
    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final ProductTypeRepository productTypeRepository;
    private final WarehouseRepository warehouseRepository;

    private final GuideNumberGenerator guideNumberGenerator;

    public LandShipmentServiceImpl(LandShipmentRepository landShipmentRepository,
                                   ShipmentRepository shipmentRepository,
                                   ClientRepository clientRepository,
                                   ProductTypeRepository productTypeRepository,
                                   WarehouseRepository warehouseRepository,
                                   GuideNumberGenerator guideNumberGenerator) {
        this.landShipmentRepository = landShipmentRepository;
        this.shipmentRepository = shipmentRepository;
        this.clientRepository = clientRepository;
        this.productTypeRepository = productTypeRepository;
        this.warehouseRepository = warehouseRepository;
        this.guideNumberGenerator = guideNumberGenerator;
    }

    @Override
    public LandShipmentDTO createLandShipment(LandShipmentDTO dto) {

        // Validar número de guía único
        if (shipmentRepository.existsByGuideNumber(dto.getGuideNumber())) {
            throw new DuplicateResourceException("An order with the tracking number already exists: "
                    + dto.getGuideNumber());
        }

        // Buscar entidades relacionadas
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("The client was not found whit id: "
                        + dto.getClientId()));

        ProductType productType = productTypeRepository.findById(dto.getProductTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("The product was not found with id: "
                        + dto.getProductTypeId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("The warehouse was not found with id: "
                        + dto.getWarehouseId()));

        // Calcular descuento
        BigDecimal priceDiscount = calculateDiscount(dto.getQuantity(), dto.getPrice());

        // Genera número de guía automáticamente
        String guideNumber = guideNumberGenerator.generate();

        // Crear y guardar Shipment base
        Shipment shipment = new Shipment();
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(dto.getQuantity());
        shipment.setRegistryDate(LocalDateTime.now());
        shipment.setDeliveryDate(dto.getDeliveryDate());
        shipment.setPrice(dto.getPrice());
        shipment.setGuideNumber(guideNumber);
        shipment.setPriceDiscount(priceDiscount);
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Crear y guardar LandShipment
        LandShipment landShipment = new LandShipment();
        landShipment.setShipment(savedShipment);
        landShipment.setWarehouse(warehouse);
        landShipment.setVehiclePlate(dto.getVehiclePlate().toUpperCase());
        LandShipment saved = landShipmentRepository.save(landShipment);

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandShipmentDTO> getAllLandShipments() {
        return landShipmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LandShipmentDTO getLandShipmentById(Integer id) {
        LandShipment landShipment = landShipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(GET)The LandShipment was not found with id: " + id));
        return mapToDTO(landShipment);
    }

    @Override
    public LandShipmentDTO updateLandShipment(Integer id, LandShipmentDTO dto) {
        LandShipment landShipment = landShipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE)The LandShipment was not found with id: " + id));

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE LANDSHIPMENT)The client was not found whit id: "
                        + dto.getClientId()));

        ProductType productType = productTypeRepository.findById(dto.getProductTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE LANDSHIPMENT)The product was not found with id: "
                        + dto.getProductTypeId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("(UPDATE LANDSHIPMENT)The Warehouse was not found with id: "
                        + dto.getWarehouseId()));

        // Recalcular descuento
        BigDecimal priceDiscount = calculateDiscount(dto.getQuantity(), dto.getPrice());

        // Actualizar Shipment base
        Shipment shipment = landShipment.getShipment();
        shipment.setClient(client);
        shipment.setProductType(productType);
        shipment.setQuantity(dto.getQuantity());
        shipment.setRegistryDate(dto.getRegistryDate());
        shipment.setDeliveryDate(dto.getDeliveryDate());
        shipment.setPrice(dto.getPrice());
        shipment.setGuideNumber(dto.getGuideNumber());
        shipment.setPriceDiscount(priceDiscount);
        shipmentRepository.save(shipment);

        // Actualizar LandShipment
        landShipment.setWarehouse(warehouse);
        landShipment.setVehiclePlate(dto.getVehiclePlate());
        LandShipment updated = landShipmentRepository.save(landShipment);

        return mapToDTO(updated);
    }

    @Override
    public void deleteLandShipment(Integer id) {
        LandShipment landShipment = landShipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The LandShipment was not found with id: " + id));

        landShipmentRepository.delete(landShipment);
        shipmentRepository.delete(landShipment.getShipment());
    }

    // ---- Lógica de descuento ----
    private BigDecimal calculateDiscount(Integer quantity, BigDecimal price) {
        if (quantity > DISCOUNT_THRESHOLD) {
            BigDecimal discount = price.multiply(DISCOUNT_RATE);
            return price.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

    // ---- Mapeos ----
    private LandShipmentDTO mapToDTO(LandShipment landShipment) {
        Shipment s = landShipment.getShipment();
        return new LandShipmentDTO(
                s.getId(),
                s.getClient().getId(),
                s.getProductType().getId(),
                s.getQuantity(),
                s.getRegistryDate(),
                s.getDeliveryDate(),
                s.getPrice(),
                s.getGuideNumber(),
                s.getPriceDiscount(),
                landShipment.getWarehouse().getId(),
                landShipment.getVehiclePlate()
        );
    }
}
