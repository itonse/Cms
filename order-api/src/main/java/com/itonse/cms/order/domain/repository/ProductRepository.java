package com.itonse.cms.order.domain.repository;

import com.itonse.cms.order.domain.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {    // ProductRepositoryCustom 을 확장함

    @EntityGraph(attributePaths = {"productItems"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Product> findBySellerIdAndId(Long sellerId, Long id);

    // product 에 있는 'productItems' 를 같이 가져올 것
    @EntityGraph(attributePaths = {"productItems"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Product> findWithProductItemsById(Long id);

    @EntityGraph(attributePaths = {"productItems"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Product> findAllByIdIn(List<Long> ids);

}
