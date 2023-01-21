package com.project.bank1.service.interfaces;

import com.project.bank1.dto.*;
import com.project.bank1.model.Client;

public interface ClientService {
    UserTokenStateDto registerClient(ClientRegistrationDto dto) throws Exception;

    UserTokenStateDto login(LoginDto dto) throws Exception;

    boolean validateMerchantData(String merchantId, String merchantPassword);

    ClientDto getClient(String email) throws Exception;

    Client getByEmail(String email);
}
