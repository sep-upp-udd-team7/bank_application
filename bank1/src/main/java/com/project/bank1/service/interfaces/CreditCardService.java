package com.project.bank1.service.interfaces;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.model.CreditCard;

public interface CreditCardService {
    CreditCard addCreditCard(String clientsName);

    CreditCard validateIssuerCreditCard(IssuerRequestDto dto);

    CreditCard findByPan(String pan);
}
