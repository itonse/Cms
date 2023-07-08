package com.itonse.cms.order.domain.repository;

import com.itonse.cms.order.domain.model.Product;
import com.itonse.cms.order.domain.model.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{     // 커스텀 레파지토리 구현체

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> searchByName(String name) {
        String search = "%" + name + "%";    // 앞,뒤에 % 구분자 붙이기

        QProduct product = QProduct.product;

        return queryFactory.selectFrom(product)    // QProduct 엔티티를 사용하여 쿼리작성 시작
                .where(product.name.like(search))    // 상품의 이름이 사용자가 입력한 검색어와 일치하는지 확인
                .fetch();    // 쿼리 수행하고 결과를 Product 리스트로 반환
    }
}
