package com.itonse.cms.user.service.test;

import com.itonse.cms.user.client.MailgunClient;
import com.itonse.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;

    public String sendEmail() {

        SendMailForm form = SendMailForm.builder()
                .from("zerobase-test@dannyEmail.com")
                .to("govl3dnjs@gmail.com")
                .subject("Test email from zerobase")
                .text("my text")
                .build();

        return mailgunClient.sendEmail(form).getBody();
    }
}
