package com.itonse.cms.order.domain.repository;

import com.itonse.cms.order.domain.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {      // 커스텀 레파지토리
    List<Product> searchByName(String name);
}
