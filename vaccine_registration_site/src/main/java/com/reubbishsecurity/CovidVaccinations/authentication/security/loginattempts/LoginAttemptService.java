package com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.*;

@Service
public class LoginAttemptService {
    private static Logger log = LogManager.getLogger(LoginAttemptService.class);

    private final int MAX_ATTEMPT = 3;
    private final int TIMEOUT_LENGTH_MINS = 20;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(TIMEOUT_LENGTH_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key) {
        log.info("LoginAttemptService: loginSucceeded| User: " + key + " removing attempts!");
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            log.info("LoginAttemptService: loginFailed| User: " + key + " not found!");
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
        log.info("LoginAttemptService: loginFailed| User: " + key + ", Attempts: "  + attempts);
    }

    public boolean isBlocked(String key) {
        try {
            int attempts = attemptsCache.get(key);
            log.info("LoginAttemptService: isBlocked| User: " + key + ", Attempts: "  + attempts);
            return attempts >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            log.warn("LoginAttemptService: isBlocked| User: " + key + " not found!");
            return false;
        }
    }
}