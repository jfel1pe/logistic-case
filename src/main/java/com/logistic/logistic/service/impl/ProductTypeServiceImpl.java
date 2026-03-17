package com.logistic.logistic.service.impl;

import com.logistic.logistic.dto.ProductTypeDTO;
import com.logistic.logistic.entity.ProductType;
import com.logistic.logistic.repository.ProductTypeRepository;
import com.logistic.logistic.service.ProductTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductTypeServiceImpl implements ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    @Override
    public ProductTypeDTO createProductType(ProductTypeDTO productTypeDTO) {
        ProductType productType = mapToEntity(productTypeDTO);
        ProductType saved = productTypeRepository.save(productType);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTypeDTO> getAllProductTypes() {
        return productTypeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductTypeDTO getProductTypeById(Integer id) {
        ProductType productType = productTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(GET)The product was not found: " + id));
        return mapToDTO(productType);
    }

    @Override
    public ProductTypeDTO updateProductType(Integer id, ProductTypeDTO productTypeDTO) {
        ProductType productType = productTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("(UPDATE)The product was not found: " + id));

        productType.setName(productTypeDTO.getName());
        productType.setDescription(productTypeDTO.getDescription());

        ProductType updated = productTypeRepository.save(productType);
        return mapToDTO(updated);
    }

    @Override
    public void deleteProductType(Integer id) {
        if (!productTypeRepository.existsById(id)) {
            throw new RuntimeException("(DELETE)The product was not found: " + id);
        }
        productTypeRepository.deleteById(id);
    }

    private ProductTypeDTO mapToDTO(ProductType productType) {
        return new ProductTypeDTO(
                productType.getId(),
                productType.getName(),
                productType.getDescription()
        );
    }

    private ProductType mapToEntity(ProductTypeDTO dto) {
        return new ProductType(
                dto.getId(),
                dto.getName(),
                dto.getDescription()
        );
    }
}
