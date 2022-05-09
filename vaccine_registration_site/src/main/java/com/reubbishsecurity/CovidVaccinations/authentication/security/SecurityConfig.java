package com.reubbishsecurity.CovidVaccinations.authentication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

// @Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Resource
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Resource
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/resources/**", "/images/**", "/css/**", "/register", "/statistics/activity", "/statistics/peractivity").permitAll()
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
                    .loginPage("/login")
                    .loginProcessingUrl("/processLogin")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .usernameParameter("pps").passwordParameter("password")
                    .permitAll()
                .authenticationDetailsSource(authenticationDetailsSource)
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(bCryptPasswordEncoder);

        auth.authenticationProvider(customAuthenticationProvider);
    }

}
