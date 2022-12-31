package com.project.bank1.controller;

import com.project.bank1.dto.ClientRegistrationDto;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.dto.TestDto;
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

@RestController
@RequestMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CreditCardService creditCardService;

    @RequestMapping(method = RequestMethod.GET, value = "/getAll")
    public ResponseEntity<?> getAllClients() {

        return new ResponseEntity<>(creditCardService.addCreditCard("Sanja Drinic"), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public ResponseEntity<?> registerClient(@RequestBody @Validated ClientRegistrationDto dto) {
        try {
            return new ResponseEntity<>(clientService.registerClient(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            return new ResponseEntity<>(clientService.login(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: obrisati - test endpoint za Bonitu
    @RequestMapping(method = RequestMethod.POST, value = "/pay")
    public Boolean testPay(@RequestBody TestDto dto) {
        System.out.println("AAAAAA" + dto.getAmount());
        return true;
    }

}
