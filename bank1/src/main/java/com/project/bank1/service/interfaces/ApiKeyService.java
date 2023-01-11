package com.project.bank1.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.bank1.dto.MerchantCredentialsDto;

public interface ApiKeyService {

    String generateApiKey(MerchantCredentialsDto dto) throws Exception;
}
