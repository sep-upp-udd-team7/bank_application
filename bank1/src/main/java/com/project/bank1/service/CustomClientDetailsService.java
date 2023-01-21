package com.project.bank1.service;

import com.project.bank1.model.Client;
import com.project.bank1.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class CustomClientDetailsService implements UserDetailsService {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client user = clientRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(MessageFormat.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }
}
