package com.project.bank1.controller;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.PccRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.exceptions.ErrorTransactionException;
import com.project.bank1.exceptions.FailedTransactionException;
import com.project.bank1.service.LoggerService;
import com.project.bank1.service.interfaces.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountController {
    private LoggerService loggerService = new LoggerService(this.getClass());
    @Autowired
    private BankAccountService bankAccountService;

    @RequestMapping(method = RequestMethod.POST, value = "/validateAcquirer", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateAcquirer(@RequestBody RequestDto dto) {
        loggerService.infoLog(MessageFormat.format("Acquirer validation by merchant ID: {0}", dto.getMerchantId()));
        try {
            return new ResponseEntity<>(bankAccountService.validateAcquirer(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/validateIssuer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateIssuer(@RequestBody IssuerRequestDto dto) {
        loggerService.infoLog(MessageFormat.format("Issuer validation for payment ID: {0}", dto.getPaymentId()));
        return new ResponseEntity<>(bankAccountService.validateIssuer(dto), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/issuerBankPayment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> issuerPaymentDifferentBanks(@RequestBody PccRequestDto dto) {
        System.out.println("Bank 2 controller from pcc request.......");
        try {
            return new ResponseEntity<>(bankAccountService.issuerPaymentDifferentBanks(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
