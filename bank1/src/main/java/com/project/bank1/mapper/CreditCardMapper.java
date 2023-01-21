package com.project.bank1.mapper;

import com.project.bank1.dto.CreditCardDto;
import com.project.bank1.model.CreditCard;

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
