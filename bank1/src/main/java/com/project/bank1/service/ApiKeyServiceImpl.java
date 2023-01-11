package com.project.bank1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bank1.dto.ApiKeyDto;
import com.project.bank1.dto.MerchantCredentialsDto;
import com.project.bank1.service.interfaces.ApiKeyService;
import com.project.bank1.service.interfaces.ClientService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {
    private LoggerService loggerService = new LoggerService(this.getClass());
    @Autowired
    private ClientService clientService;

    @Override
    public String generateApiKey(MerchantCredentialsDto dto) throws Exception {
        if (clientService.validateMerchantData(dto.getMerchantId(), dto.getMerchantPassword())) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(getApiKeyDto(dto.getMerchantId(), dto.getBankName()));
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            String apiKey = Base64.getUrlEncoder().encodeToString(bytes);
            System.out.println(apiKey);

            // decoding
            byte [] bytesDecoding = Base64.getDecoder().decode(apiKey);
            String jsonDecoding = new String(bytesDecoding, StandardCharsets.UTF_8);
            ObjectMapper objectMapperDecoding = new ObjectMapper();
            ApiKeyDto data = objectMapperDecoding.readValue(jsonDecoding, ApiKeyDto.class);
            System.out.println("Decoded " + data.getMerchantId() + " " + data.getBankName());

            return apiKey;
        }
        loggerService.warnLog("Invalid merchant credentials entered by merchant with ID: " + dto.getMerchantId() );
        throw new Exception("Invalid merchant credentials");
    }

    private ApiKeyDto getApiKeyDto(String merchantId, String bankName) {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setMerchantId(merchantId);
        dto.setBankName(bankName);
        return dto;
    }


}
