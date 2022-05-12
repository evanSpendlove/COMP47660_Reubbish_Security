package com.reubbishsecurity.CovidVaccinations.authentication.security;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.Role;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.RoleRepository;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts.LoginAttemptService;
import com.reubbishsecurity.CovidVaccinations.authentication.security.loginattempts.LoginUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static Logger log = LogManager.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String pps_no) {
        log.info("CustomUserDetailsService: loadUserByUsername| Received PPS: " + pps_no);

        String ip = LoginUtils.getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            log.info("CustomUserDetailsService: loadUserByUsername| IP: " + ip);
            throw new RuntimeException("blocked");
        }

        try {
            User user = userRepository.findByPps(pps_no).orElseThrow(() -> new UsernameNotFoundException("User not found with pps_no = " + pps_no));

            log.info("CustomUserDetailsService: loadUserByUsername| User found: " + user);
            List<Role> authorities = new ArrayList<Role>(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } catch (UsernameNotFoundException e){
            log.info("CustomUserDetailsService: loadUserByUsername| User not found!", e);
            throw e;
        } catch (Exception e) {
            log.error("CustomUserDetailsService: loadUserByUsername| Unexpected Exception!", e);
            throw new RuntimeException(e);
        }

    }

    private UserDetails buildUserForAuthentication(User user, List<Role> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getPps(), user.getPassword(), authorities
                .stream()
                .map((Role r) -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList()));
    }
}
