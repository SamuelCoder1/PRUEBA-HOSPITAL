package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String document) throws UsernameNotFoundException {
        System.out.println("Searching for user with document: " + document); // Para depuración

        Optional<User> userOptional = userRepository.findByDocument(document);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with document: " + document);
        }

        System.out.println("username found: " + userOptional);
        return userOptional.get();
    }
}
