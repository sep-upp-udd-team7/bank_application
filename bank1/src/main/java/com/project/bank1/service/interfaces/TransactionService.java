package com.project.bank1.service.interfaces;

import com.project.bank1.dto.RequestDto;
import com.project.bank1.model.Transaction;

public interface TransactionService {
    Transaction createAcquirerTransaction(RequestDto dto);
}
