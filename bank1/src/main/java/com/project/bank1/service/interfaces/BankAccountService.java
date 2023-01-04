package com.project.bank1.service.interfaces;

import com.project.bank1.dto.*;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;

public interface BankAccountService {
    BankAccount addBankAccount(Client client);

    AcquirerResponseDto validateAcquirer(RequestDto dto) throws Exception;

    BankAccount findBankAccountByMerchantId(String merchantId);

    String validateIssuer(IssuerRequestDto dto);

    PccResponseDto issuerPaymentDifferentBanks(PccRequestDto dto);

    BankAccount findBankAccountByCreditCardId(Long creditCardId);

}
