package com.project.bank1.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.model.CreditCard;

public interface CreditCardService {
    CreditCard addCreditCard(String clientsName);

    CreditCard validateIssuerCreditCard(IssuerRequestDto dto);

    CreditCard findByPan(String pan);

    String decodePan(String encodedPan);

    String encodePan(String pan);
}
