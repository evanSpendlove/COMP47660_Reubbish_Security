package com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class LoginUtils {

    private static Logger log = LogManager.getLogger(LoginUtils.class);

    public static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");

        log.info("LoginUtils: getClientIP| xfHeader: " + xfHeader);

        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        else {
            return xfHeader.split(",")[0];
        }
    }
}
