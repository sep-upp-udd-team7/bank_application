package com.project.bank1.controller;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.service.interfaces.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountController {

    @Autowired
    BankAccountService bankAccountService;

    @RequestMapping(method = RequestMethod.POST, value = "/validateAcquirer", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateAcquirer(@RequestBody RequestDto dto) {
        try {
            return new ResponseEntity<>(bankAccountService.validateAcquirer(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/validateIssuer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateIssuer(@RequestBody IssuerRequestDto dto) {
        try {
            return new ResponseEntity<>(bankAccountService.validateIssuer(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
