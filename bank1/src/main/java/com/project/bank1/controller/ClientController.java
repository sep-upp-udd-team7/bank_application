package com.project.bank1.controller;

import com.project.bank1.dto.ClientRegistrationDto;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.dto.TestDto;
import com.project.bank1.service.LoggerService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.MessageFormat;

@RestController
@RequestMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    private LoggerService loggerService = new LoggerService(this.getClass());
    @Autowired
    private ClientService clientService;

    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public ResponseEntity<?> registerClient(@RequestBody @Validated ClientRegistrationDto dto) {
        loggerService.infoLog(MessageFormat.format("Client registration with email: {0}", dto.getEmail()));
        try {
            return new ResponseEntity<>(clientService.registerClient(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        loggerService.infoLog(MessageFormat.format("Login with email: {0}", dto.getEmail()));
        try {
            return new ResponseEntity<>(clientService.login(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getLoggedUser(Principal user) {
        loggerService.infoLog(MessageFormat.format("Get logged user by email: {0}", user.getName()));
        try {
            return new ResponseEntity<>(clientService.getClient(user.getName()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
