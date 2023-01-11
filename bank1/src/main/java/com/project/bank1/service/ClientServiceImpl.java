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
import java.text.MessageFormat;
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
    private LoggerService loggerService = new LoggerService(this.getClass());
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
        loggerService.debugLog(MessageFormat.format("Register client with email: {0}", dto.getEmail()));
        Client client = new ClientMapper().mapClientRegistrationDtoToClient(dto);
        if(getAllEmails().contains(dto.getEmail())) {
            loggerService.errorLog(MessageFormat.format("Email {0} is not unique", dto.getEmail()));
            throw new Exception("Email is not unique");
        }
        if(!dto.getPassword().equals(dto.getReenteredPassword())){
            loggerService.errorLog("Passwords do not match");
            throw new Exception("Passwords do not match");
        }
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setLastPasswordResetDate(Timestamp.from(Instant.now().minusSeconds(1)));
        ClientType clientType = clientTypeService.findClientTypeByName(findClientTypeName(dto.getIsCompany()));
        if (clientType == null) {
            loggerService.errorLog("Client type not found");
            throw new Exception("Client type not found");
        }
        client.setClientType(clientType);
        if (dto.getIsCompany()) {
            loggerService.debugLog(MessageFormat.format("Client with email: {0} is acquirer", dto.getEmail()));
            client.setMerchantId(generateRandomString(merchantIdStringLength));
            client.setMerchantPassword(generateRandomString(merchantPasswordStringLength));
        }
        clientRepository.save(client);
        loggerService.successLog(MessageFormat.format("Client with email: {0} is successfully registered", dto.getEmail()));
        Client c = clientRepository.findByEmail(client.getEmail());
        client.setBankAccount(bankAccountService.addBankAccount(c));
        VerificationToken verificationToken = new VerificationToken(client);
        verificationTokenService.saveVerificationToken(verificationToken);

        String jwt = tokenUtils.generateToken(client.getUsername(), client.getClientType().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return new UserTokenStateDto(jwt, expiresIn);
    }

    private String generateRandomString(int targetStringLength) {
        loggerService.infoLog(MessageFormat.format("Generating random string of length {0} characters", targetStringLength));
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    private Collection<String> getAllEmails() {
        Collection<String> emails = new ArrayList<>();
        for (Client c: clientRepository.findAll()) {
            emails.add(c.getEmail());
        }
        loggerService.debugLog(MessageFormat.format("Getting all emails from client with size {0}", emails.size()));
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
        loggerService.infoLog(MessageFormat.format("Login by email: {0}", dto.getEmail()));
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(), dto.getPassword()));
        } catch (Exception ex) {
            loggerService.warnLog(MessageFormat.format("Bad credentials entered by client with email: {0}", dto.getEmail()));
            throw new Exception("Bad credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Client client = (Client) authentication.getPrincipal();

        String jwt = tokenUtils.generateToken(client.getUsername(), client.getClientType().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        loggerService.successLog(MessageFormat.format("Successful login by email: {0}, JWT token is generated", dto.getEmail()));
        return new UserTokenStateDto(jwt, expiresIn);
    }

    @Override
    public boolean validateMerchantData(String merchantId, String merchantPassword) {
        loggerService.infoLog(MessageFormat.format("Validation merchant credentials with merchant ID: {0} and merchant password", merchantId));
        Client client = clientRepository.findByMerchantId(merchantId);
        if (client == null) {
            loggerService.errorLog("Client is not found or client is not registered as company");
            return false;
        }
        if (!client.getMerchantPassword().equals(merchantPassword)) {
            loggerService.warnLog(MessageFormat.format("Client with merchant ID: {0} entered wrong password", merchantId));
            return  false;
        }
        return true;
    }

    @Override
    public ClientDto getClient(String email) throws Exception {
        loggerService.infoLog("Get client by email: " + email);
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            loggerService.errorLog(MessageFormat.format("Client with email: {0} not found", email));
            throw new Exception("Client with " + email + " not found!");
        }
        loggerService.successLog(MessageFormat.format("Client with email: {0} found", email));
        return new ClientMapper().mapClientToClientDto(client);
    }

    @Override
    public Client getByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


}
