package com.project.bank1.service.interfaces;

import com.project.bank1.dto.ClientRegistrationDto;

public interface ClientService {
    void registerClient(ClientRegistrationDto dto) throws Exception;
}
