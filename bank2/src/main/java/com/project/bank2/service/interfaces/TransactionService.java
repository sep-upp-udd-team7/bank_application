package com.project.bank2.service.interfaces;

import com.project.bank2.dto.RequestDto;
import com.project.bank2.enums.TransactionStatus;
import com.project.bank2.model.Transaction;

public interface TransactionService {
    Transaction createTransaction(RequestDto dto);

    void save(Transaction transaction);

    Transaction findByPaymentId(String paymentId);

    void updateStatus(String id, TransactionStatus failed);
}
