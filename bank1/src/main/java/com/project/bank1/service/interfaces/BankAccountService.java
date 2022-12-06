package com.project.bank1.service.interfaces;

import com.project.bank1.dto.AcquirerResponseDto;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;

public interface BankAccountService {
    BankAccount addBankAccount(Client client);

    AcquirerResponseDto validateAcquirer(RequestDto dto);

    Object validateIssuer(IssuerRequestDto dto);

    BankAccount findBankAccountByMerchantId(String merchantId);
}
