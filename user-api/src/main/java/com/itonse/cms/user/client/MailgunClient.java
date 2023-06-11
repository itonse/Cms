package com.itonse.cms.user.client;

import com.itonse.cms.user.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")   // 메일건, 공통url
@Qualifier("mailgun")
public interface MailgunClient {

    @PostMapping("sandboxc8c7e58bdb614d9092b5ec400f7054e1.mailgun.org/messages")
    ResponseEntity<String> sendEmail(@SpringQueryMap SendMailForm form);
}
