package com.itonse.cms.user.client.mailgun;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter   // 빌더패턴을 사용하기 때문에 Setter 어노테이션은 필요 없다.
@Builder
public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
