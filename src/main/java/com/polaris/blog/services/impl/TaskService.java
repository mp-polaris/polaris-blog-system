package com.polaris.blog.services.impl;

import com.polaris.blog.utils.EmailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Service
public class TaskService {
    @Async
    public void sendEmailVerifyCode(String verifyCode, String emailAddress) throws MessagingException {
        EmailSender.sendRegisterVerifyCode(verifyCode,emailAddress);
    }
}
