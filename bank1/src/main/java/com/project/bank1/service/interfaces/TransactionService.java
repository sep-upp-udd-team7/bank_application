package com.project.bank1.service.interfaces;

import com.project.bank1.dto.RequestDto;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Transaction;

public interface TransactionService {
    Transaction createTransaction(RequestDto dto);

    void save(Transaction transaction);

    Transaction findByPaymentId(String paymentId);
}
