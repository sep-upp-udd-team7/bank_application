package com.project.bank1.service;

import com.project.bank1.dto.*;
import com.project.bank1.mapper.ClientMapper;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;
import com.project.bank1.model.ClientType;
import com.project.bank1.model.VerificationToken;
import com.project.bank1.repository.ClientRepository;
import com.project.bank1.security.util.TokenUtils;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.ClientTypeService;
import com.project.bank1.service.interfaces.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientTypeService clientTypeService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public UserTokenStateDto registerClient(ClientRegistrationDto dto) throws Exception {
        Client client = new ClientMapper().mapClientRegistrationDtoToClient(dto);
        if(getAllEmails().contains(dto.getEmail())) {
            throw new Exception("Email is not unique");
        }
        if(!dto.getPassword().equals(dto.getReenteredPassword())){
            throw new Exception("Passwords do not match");
        }
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setLastPasswordResetDate(Timestamp.from(Instant.now()));
        ClientType clientType = clientTypeService.findClientTypeByName(findClientTypeName(dto.getIsCompany()));
        if (clientType == null) {
            throw new Exception("Client type is not found");
        }
        client.setClientType(clientType);
        // TODO SD: generisati merchant id i merchant password
        if (dto.getIsCompany()) {
            client.setMerchantId("1");
            client.setMerchantPassword("1011");
        }
        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccountService.addBankAccount(client));
        client.setBankAccounts(bankAccounts);
        VerificationToken verificationToken = new VerificationToken(client);
        verificationTokenService.saveVerificationToken(verificationToken);
        clientRepository.save(client);

        String jwt = tokenUtils.generateToken(client.getUsername(), client.getClientType().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return new UserTokenStateDto(jwt, expiresIn);
    }

    private Collection<String> getAllEmails() {
        Collection<String> emails = new ArrayList<>();
        for (Client c: clientRepository.findAll()) {
            emails.add(c.getEmail());
        }
        return emails;
    }

    private String findClientTypeName(boolean isCompany) {
        if (isCompany) {
            return "ROLE_COMPANY";
        } else {
            return "ROLE_CLIENT";
        }
    }

    @Override
    public UserTokenStateDto login(LoginDto dto) throws Exception {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(), dto.getPassword()));
        } catch (Exception ex) {
            throw new Exception("Bad credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Client client = (Client) authentication.getPrincipal();

        String jwt = tokenUtils.generateToken(client.getUsername(), client.getClientType().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return new UserTokenStateDto(jwt, expiresIn);
    }


}
