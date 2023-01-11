package com.project.bank2.service.interfaces;

import com.project.bank2.dto.ClientDto;
import com.project.bank2.dto.ClientRegistrationDto;
import com.project.bank2.dto.LoginDto;
import com.project.bank2.dto.UserTokenStateDto;

public interface ClientService {
    UserTokenStateDto registerClient(ClientRegistrationDto dto) throws Exception;

    UserTokenStateDto login(LoginDto dto) throws Exception;

    boolean validateMerchantData(String merchantId, String merchantPassword);

    ClientDto getClient(String email) throws Exception;
}
