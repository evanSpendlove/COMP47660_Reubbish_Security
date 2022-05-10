package com.reubbishsecurity.CovidVaccinations.authentication.security;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.Role;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.exception.UserNotFoundException;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String pps_no) {
        System.out.println("Received pps_no = " + pps_no);
        User user = userRepository.findByPps(pps_no).orElseThrow(() -> new UsernameNotFoundException("User not found with pps_no = " + pps_no));
        System.out.println("User found = " + user);
        List<Role> authorities = new ArrayList<Role>(user.getRoles());
        return buildUserForAuthentication(user, authorities);
    }

    private UserDetails buildUserForAuthentication(User user, List<Role> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getPps(), user.getPassword(), authorities
                .stream()
                .map((Role r) -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList()));
    }

}
