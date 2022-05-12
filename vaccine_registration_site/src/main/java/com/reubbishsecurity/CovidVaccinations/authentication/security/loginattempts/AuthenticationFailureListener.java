package com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener/*TODO <LoginAttemptService>*/ implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static Logger log = LogManager.getLogger(AuthenticationSuccessEventListener.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        String ip = LoginUtils.getClientIP(request);
        log.info("AuthenticationFailureListener: onApplicationEvent| IP: " + ip + ", Event: " + e);
        loginAttemptService.loginFailed(ip);
    }
}