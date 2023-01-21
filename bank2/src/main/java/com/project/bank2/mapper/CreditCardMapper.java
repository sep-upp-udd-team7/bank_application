package com.project.bank2.mapper;

import com.project.bank2.dto.CreditCardDto;
import com.project.bank2.model.CreditCard;

public class CreditCardMapper {
    public CreditCardDto mapCreditCardToCreditCardDto(CreditCard cc) {
        CreditCardDto dto = new CreditCardDto();
        dto.setId(cc.getId());
        dto.setCardHolderName(cc.getCardHolderName());
        dto.setPan(cc.getPan());
        dto.setMm(cc.getMm());
        dto.setYy(cc.getYy());
        dto.setCvv(cc.getCvv());
        return dto;
    }
}
