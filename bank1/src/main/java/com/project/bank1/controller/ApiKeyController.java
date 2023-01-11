package com.project.bank1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.bank1.dto.ApiKeyDto;
import com.project.bank1.dto.MerchantCredentialsDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.service.LoggerService;
import com.project.bank1.service.interfaces.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api-keys", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiKeyController {
    private LoggerService loggerService = new LoggerService(this.getClass());
    @Autowired
    private ApiKeyService apiKeyService;

    @RequestMapping(method = RequestMethod.POST, value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateApiKey(@RequestBody MerchantCredentialsDto dto) {
        loggerService.infoLog("Generating API key for merchant with ID: " + dto.getMerchantId());
        try {
            return new ResponseEntity<>(apiKeyService.generateApiKey(dto), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> decodeApiKey(@RequestBody String apiKey) {
        System.out.printf(apiKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
