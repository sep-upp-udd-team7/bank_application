package com.project.bank2.mapper;

import com.project.bank2.dto.BankAccountDto;
import com.project.bank2.model.BankAccount;

public class BankMapper {

    public BankAccountDto mapBankAccountToBankAccountDto(BankAccount ba) {
        BankAccountDto dto = new BankAccountDto();
        dto.setId(ba.getId());
        if (ba.getCreditCard() != null) {
            dto.setCreditCard(new CreditCardMapper().mapCreditCardToCreditCardDto(ba.getCreditCard()));
        }
        dto.setBankAccountNumber(ba.getBankAccountNumber());
        dto.setReservedFunds(ba.getReservedFunds());
        dto.setAvailableFunds(ba.getAvailableFunds());
        return dto;
    }
}
