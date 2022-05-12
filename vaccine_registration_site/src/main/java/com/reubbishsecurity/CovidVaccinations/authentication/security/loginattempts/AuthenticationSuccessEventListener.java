package com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {
    private static Logger log = LogManager.getLogger(AuthenticationSuccessEventListener.class);


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        String ip = LoginUtils.getClientIP(request);
        log.info("AuthenticationSuccessEventListener: onApplicationEvent| IP: " + ip + ", Event: " + e);
        loginAttemptService.loginSucceeded(ip);
    }
}
