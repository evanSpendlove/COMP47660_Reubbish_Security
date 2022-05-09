package com.reubbishsecurity.CovidVaccinations.authentication.multifactor;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("mfaTokenManager")
public class DefaultMFATokenManager implements MFATokenManager {

    private SecretGenerator secretGenerator = new DefaultSecretGenerator();

    @Resource
    private QrGenerator qrGenerator;

    @Resource
    private CodeVerifier codeVerifier;

    @Override
    public String generateSecretKey() {
        return secretGenerator.generate();
    }

    @Override
    public String getQRCode(String secret) throws QrGenerationException {
        QrData data = new QrData.Builder().label("MFA")
                .secret(secret)
                .issuer("HSE COVID-19 Portal")
                .algorithm(HashingAlgorithm.SHA256)
                .digits(6)
                .period(30)
                .build();
        return  Utils.getDataUriForImage(
                qrGenerator.generate(data),
                qrGenerator.getImageMimeType()
        );
    }

    @Override
    public boolean verifyTotp(String code, String secret) {
        return codeVerifier.isValidCode(secret, code);
    }
}
