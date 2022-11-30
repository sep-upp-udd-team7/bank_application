package com.project.bank1.controller;

import com.project.bank1.dto.RequestDto;
import com.project.bank1.service.interfaces.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountController {

    @Autowired
    BankAccountService bankAccountService;

    @RequestMapping(method = RequestMethod.GET, value = "/validate")
    public ResponseEntity<?> validateAcquirer(RequestDto dto) {
        return new ResponseEntity<>("OK!", HttpStatus.OK);
    }

}
