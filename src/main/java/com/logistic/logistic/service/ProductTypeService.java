package com.logistic.logistic.service;

import com.logistic.logistic.dto.ProductTypeDTO;
import java.util.List;

public interface ProductTypeService {

    ProductTypeDTO createProductType(ProductTypeDTO productTypeDTO);
    List<ProductTypeDTO> getAllProductTypes();
    ProductTypeDTO getProductTypeById(Integer id);
    ProductTypeDTO updateProductType(Integer id, ProductTypeDTO productTypeDTO);
    void deleteProductType(Integer id);
}
