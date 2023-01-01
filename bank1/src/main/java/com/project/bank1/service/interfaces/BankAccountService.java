package com.project.bank1.service.interfaces;

import com.project.bank1.dto.AcquirerResponseDto;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.exceptions.ErrorTransactionException;
import com.project.bank1.exceptions.FailedTransactionException;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;

public interface BankAccountService {
    BankAccount addBankAccount(Client client);

    AcquirerResponseDto validateAcquirer(RequestDto dto) throws Exception;

    BankAccount findBankAccountByMerchantId(String merchantId);

    String validateIssuer(IssuerRequestDto dto) throws FailedTransactionException, Exception;
}
