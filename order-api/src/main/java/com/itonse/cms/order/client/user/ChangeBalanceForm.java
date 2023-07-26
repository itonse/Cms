package com.itonse.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
