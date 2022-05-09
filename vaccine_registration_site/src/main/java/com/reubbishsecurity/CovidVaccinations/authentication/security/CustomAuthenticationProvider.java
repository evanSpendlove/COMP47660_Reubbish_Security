package com.reubbishsecurity.CovidVaccinations.authentication.security;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.multifactor.DefaultMFATokenManager;
import com.reubbishsecurity.CovidVaccinations.authentication.multifactor.MFATokenManager;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Resource
    private MFATokenManager mfaTokenManager;

    @Autowired
    public CustomAuthenticationProvider(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        super.additionalAuthenticationChecks(userDetails, authentication);

        CustomWebAuthenticationDetails authenticationDetails = (CustomWebAuthenticationDetails) authentication.getDetails();
        User user = (User) userDetails;
        String mfaToken = authenticationDetails.getToken();
        if(!mfaTokenManager.verifyTotp(mfaToken,user.getSecret())){
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }


}

