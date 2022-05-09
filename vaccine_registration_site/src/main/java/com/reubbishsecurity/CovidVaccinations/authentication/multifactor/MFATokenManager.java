package com.reubbishsecurity.CovidVaccinations.authentication.multifactor;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface MFATokenManager {
    String generateSecretKey();
    String getQRCode(final String secret) throws QrGenerationException;
    boolean verifyTotp(final String code, final String secret);
}