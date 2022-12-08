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

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ClientServiceImpl implements ClientService {
    private static int merchantIdStringLength = 30;
    private static int merchantPasswordStringLength = 100;
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
        if (dto.getIsCompany()) {
            String merchantId = generateRandomString(merchantIdStringLength);
            String merchantPassword = generateRandomString(merchantPasswordStringLength);
            client.setMerchantId(merchantId);
            client.setMerchantPassword(merchantPassword);
        }
        client.setBankAccounts(bankAccountService.addBankAccount(client));
        VerificationToken verificationToken = new VerificationToken(client);
        verificationTokenService.saveVerificationToken(verificationToken);
        clientRepository.save(client);

        String jwt = tokenUtils.generateToken(client.getUsername(), client.getClientType().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return new UserTokenStateDto(jwt, expiresIn);
    }

    private String generateRandomString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println(generatedString);
        return generatedString;
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

    @Override
    public boolean validateMerchantData(String merchantId, String merchantPassword) {
        Client client = clientRepository.findByMerchantId(merchantId);
        if (client == null) {
            System.out.println("Client is not found or client is not registered as company");
            return false;
        }
        if (!client.getMerchantPassword().equals(merchantPassword)) {
            return  false;
        }
        return true;
    }


}
