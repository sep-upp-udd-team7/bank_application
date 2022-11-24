package com.project.bank1.mapper;

import com.project.bank1.dto.ClientRegistrationDto;
import com.project.bank1.model.Client;

public class ClientMapper {

    public Client mapClientRegistrationDtoToClient(ClientRegistrationDto dto) {
        Client client = new Client();
        client.setEmail(dto.getEmail());
        client.setName(dto.getName());
        return client;
    }
}
