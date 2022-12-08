package com.project.bank1.service.interfaces;

import com.project.bank1.dto.RequestDto;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Transaction;

public interface TransactionService {
    Transaction createAcquirerTransaction(RequestDto dto);

    Transaction createIssuerTransaction(RequestDto dto, BankAccount issuerBankAccount);

    void save(Transaction transaction);

    Transaction findByPaymentId(String paymentId);
}
