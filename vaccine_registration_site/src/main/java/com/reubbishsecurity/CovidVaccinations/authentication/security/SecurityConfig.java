package com.reubbishsecurity.CovidVaccinations.authentication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/resources/**", "/images/**", "/css/**", "/register", "/statistics").permitAll()
                    .antMatchers("/")
                        .hasAnyAuthority("ADMIN", "USER", "VACCINATOR", "STAFF")
                    .antMatchers("/admin")
                            .hasAuthority("ADMIN")
                            .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/processLogin")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .usernameParameter("pps").passwordParameter("password")
                    .permitAll()
                .and()
                    .csrf().disable() // TODO: Add checks for CSRF and XSS attacks
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
                // .passwordEncoder(new CustomPasswordEncoder()); // TODO(evanSpendlove): Delete
                .passwordEncoder(bCryptPasswordEncoder);
    }

}
