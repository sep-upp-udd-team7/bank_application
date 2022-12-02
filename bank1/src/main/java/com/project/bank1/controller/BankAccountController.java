package com.project.bank1.controller;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.service.interfaces.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountController {

    @Autowired
    BankAccountService bankAccountService;

    @RequestMapping(method = RequestMethod.POST, value = "/validateAcquirer")
    public ResponseEntity<?> validateAcquirer(@RequestBody RequestDto dto) {
        return new ResponseEntity<>(bankAccountService.validateAcquirer(dto), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/validateIssuer")
    public ResponseEntity<?> validateIssuer(@RequestBody IssuerRequestDto dto) {
        return new ResponseEntity<>(bankAccountService.validateIssuer(dto), HttpStatus.OK);
    }

}
