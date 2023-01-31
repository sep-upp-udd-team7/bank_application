package com.project.bank1.controller;

import com.project.bank1.dto.MerchantCredentialsDto;
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
            loggerService.errorLog("Generating API key is unsuccessful");
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> decodeApiKey(@RequestBody String apiKey) {
        // TODO SD: ovaj endpoint treba obrisati, svrha je testiranje
        loggerService.infoLog("Decoding API key");
        try {
            return new ResponseEntity<>(apiKeyService.decodeApiKey(apiKey), HttpStatus.OK);
        } catch (Exception e) {
            loggerService.errorLog("Decoding API key is unsuccessful");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
