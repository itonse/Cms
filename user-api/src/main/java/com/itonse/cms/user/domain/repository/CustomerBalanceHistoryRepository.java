package com.itonse.cms.user.domain.repository;

import com.itonse.cms.user.domain.model.CustomerBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Repository
public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory, Long> {

    // @RequestParam 을 붙여서 컬럼명 명시(혼동x), customerId는 FK키
    Optional<CustomerBalanceHistory> findFirstByCustomer_IdOrderByIdDesc(
            @RequestParam("customer_id") Long customerId);
}
