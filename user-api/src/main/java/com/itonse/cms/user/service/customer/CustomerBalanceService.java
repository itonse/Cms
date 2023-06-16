package com.itonse.cms.user.service.customer;

import com.itonse.cms.user.domain.customer.ChangeBalanceForm;
import com.itonse.cms.user.domain.model.CustomerBalanceHistory;
import com.itonse.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.itonse.cms.user.domain.repository.CustomerRepository;
import com.itonse.cms.user.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itonse.cms.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.itonse.cms.user.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class CustomerBalanceService {

    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})  // 커스텀익셉션이 발생하더라도 롤백되지 않고 트랜잭션이 유지
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomException {
        CustomerBalanceHistory customerBalanceHistory =  // 가장 마지막 잔액 내역 가져오기
                customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                        .orElse(CustomerBalanceHistory.builder()  // 결제내역이 없는 경우 새로 생성
                                .changeMoney(0)
                                .currentMoney(0)
                                .customer(customerRepository.findById(customerId)
                                        .orElseThrow(() -> new CustomException(NOT_FOUND_USER)))
                                .build());

        if (customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0) {
            throw new CustomException(NOT_ENOUGH_BALANCE);
        }

        // 새로운 히스토리가 쌓임
        customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(form.getMoney())
                .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())   // 현재 잔액
                .description(form.getMessage())
                .fromMessage(form.getFrom())
                .customer(customerBalanceHistory.getCustomer())
                .build();

        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());
         // History 를 통해 customer 의 balance 값을 변경: 연관관계를 이용해 다른 객체의 값을 변경


        return customerBalanceHistoryRepository.save(customerBalanceHistory);
    }
}
