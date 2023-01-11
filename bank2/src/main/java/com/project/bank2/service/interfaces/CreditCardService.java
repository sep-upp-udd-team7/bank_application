package com.project.bank2.service.interfaces;

import com.project.bank2.dto.IssuerRequestDto;
import com.project.bank2.model.CreditCard;

public interface CreditCardService {
    CreditCard addCreditCard(String clientsName);

    CreditCard validateIssuerCreditCard(IssuerRequestDto dto);
}
