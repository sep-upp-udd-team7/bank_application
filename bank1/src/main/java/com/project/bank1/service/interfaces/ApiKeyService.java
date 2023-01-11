package com.project.bank1.service.interfaces;

import com.project.bank1.dto.ApiKeyDto;
import com.project.bank1.dto.MerchantCredentialsDto;

public interface ApiKeyService {
    String generateApiKey(MerchantCredentialsDto dto) throws Exception;

    ApiKeyDto decodeApiKey(String apiKey) throws Exception;
}
