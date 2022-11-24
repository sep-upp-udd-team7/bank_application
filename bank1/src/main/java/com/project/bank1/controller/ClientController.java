package com.project.bank1.controller;

import com.project.bank1.dto.ClientRegistrationDto;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.mapper.ClientMapper;
import com.project.bank1.model.Client;
import com.project.bank1.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    @Autowired
    private ClientService clientService;

    @RequestMapping(method = RequestMethod.GET, value = "/getAll")
    public ResponseEntity<?> getAllClients() {
        return new ResponseEntity<>("OK!", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegistrationDto dto) {
        try {
            clientService.registerClient(dto);
            return new ResponseEntity<>("Client " + dto.getEmail() + " created!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<?> registerClient(@RequestBody LoginDto dto) {
        try {
            return new ResponseEntity<>(clientService.login(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
