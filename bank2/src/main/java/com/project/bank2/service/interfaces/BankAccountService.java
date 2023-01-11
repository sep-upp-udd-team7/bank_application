package com.project.bank2.service.interfaces;

import com.project.bank2.dto.AcquirerResponseDto;
import com.project.bank2.dto.IssuerRequestDto;
import com.project.bank2.dto.RequestDto;
import com.project.bank2.model.BankAccount;
import com.project.bank2.model.Client;

public interface BankAccountService {
    BankAccount addBankAccount(Client client);

    AcquirerResponseDto validateAcquirer(RequestDto dto) throws Exception;

    BankAccount findBankAccountByMerchantId(String merchantId);

    String validateIssuer(IssuerRequestDto dto);
}
