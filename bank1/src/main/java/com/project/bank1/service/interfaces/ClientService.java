package com.project.bank1.service.interfaces;

import com.project.bank1.dto.ClientRegistrationDto;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.dto.UserTokenStateDto;

public interface ClientService {
    void registerClient(ClientRegistrationDto dto) throws Exception;

    UserTokenStateDto login(LoginDto dto) throws Exception;
}
