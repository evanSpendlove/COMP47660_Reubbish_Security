package com.reubbishsecurity.CovidVaccinations.authentication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/resources/**", "/images/**", "/css/**", "/register", "/mfa/enable", "/mfa/confirm", "/statistics/activity", "/statistics/peractivity").permitAll()
                    .antMatchers("/")
                        .hasAnyAuthority("ADMIN", "USER", "VACCINATOR", "STAFF")
                    .antMatchers("/update-roles", "/admin")
                        .hasAuthority("ADMIN")
                    .antMatchers("/portal/update-appointments", "/add/vaccination")
                        .hasAuthority("VACCINATOR")
                    .antMatchers("/forum/thread/**/post")
                        .hasAuthority("STAFF")
                        .anyRequest().authenticated()
                .and()
                .formLogin()
                    .authenticationDetailsSource(authenticationDetailsSource)
                    .loginPage("/login")
                    .loginProcessingUrl("/processLogin")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .usernameParameter("pps").passwordParameter("password")
                    .permitAll()
                .and()
                .logout().deleteCookies("JSESSIONID").clearAuthentication(true).invalidateHttpSession(true)
                .permitAll();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

}
