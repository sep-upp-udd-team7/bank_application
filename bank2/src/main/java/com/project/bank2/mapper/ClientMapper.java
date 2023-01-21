package com.project.bank2.mapper;

import com.project.bank2.dto.ClientDto;
import com.project.bank2.dto.ClientRegistrationDto;
import com.project.bank2.model.Client;

public class ClientMapper {

    public Client mapClientRegistrationDtoToClient(ClientRegistrationDto dto) {
        Client client = new Client();
        client.setEmail(dto.getEmail());
        client.setName(dto.getName());
        return client;
    }

    public ClientDto mapClientToClientDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail());
        setMerchantInformation(client, dto);
        if (client.getBankAccount() != null) {
            dto.setBankAccount(new BankMapper().mapBankAccountToBankAccountDto(client.getBankAccount()));
        }
        return dto;
    }

    private void setMerchantInformation(Client client, ClientDto dto) {
        if (client.getClientType().getName().equals("ROLE_COMPANY")) {
            dto.setMerchantId(client.getMerchantId());
            dto.setMerchantPassword(client.getMerchantPassword());
        } else {
            dto.setMerchantId("");
            dto.setMerchantPassword("");
        }
    }
}
