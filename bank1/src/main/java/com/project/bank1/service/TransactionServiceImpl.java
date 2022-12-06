package com.project.bank1.service;

import com.project.bank1.dto.RequestDto;
import com.project.bank1.enums.TransactionStatus;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Transaction;
import com.project.bank1.repository.TransactionRepository;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BankAccountService bankAccountService;

    @Override
    public Transaction createAcquirerTransaction(RequestDto dto) {
        BankAccount acquirerBankAccount = bankAccountService.findBankAccountByMerchantId(dto.getMerchantId());
        Transaction transaction = new Transaction();
        // TODO: ispraviti generisanje transaction id tj. payment id
        transaction.setId(generateTransactionId(10));
        transaction.setBankAccount(acquirerBankAccount);
        transaction.setAmount(dto.getAmount());
        transaction.setErrorURL(dto.getErrorUrl());
        transaction.setFailedURL(dto.getFailedUrl());
        transaction.setSuccessURL(dto.getSuccessUrl());
        transaction.setMerchantOrderId(dto.getMerchantOrderId());
        transaction.setMerchantTimestamp(dto.getMerchantTimestamp());
        transaction.setStatus(TransactionStatus.CREATED);
        transactionRepository.save(transaction);
        return transaction;
    }

    private Long generateTransactionId(int lengthOfPaymentId) {
        double rndNum = Math.random();
        System.out.println(rndNum);
        long number = (long) (rndNum * Math.pow(10, lengthOfPaymentId));
        System.out.println("Generated Payment ID: " + number);
        return (number);
    }
}
