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
        if (acquirerBankAccount == null) {
            System.out.println("Acquirer bank account not found!");
            return null;
        }
        Transaction transaction = new Transaction();
        // TODO: ispraviti generisanje transaction id tj. payment id
        transaction.setId(String.valueOf(generateTransactionId(10)));
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

    @Override
    public Transaction createIssuerTransaction(RequestDto dto, BankAccount issuerBankAccount) {
        Transaction transaction = new Transaction();
        // TODO: ispraviti generisanje transaction id tj. payment id
        transaction.setId(String.valueOf(generateTransactionId(10)));
        transaction.setBankAccount(issuerBankAccount);
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

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public Transaction findByPaymentId(String paymentId) {
        for (Transaction t: transactionRepository.findAll()) {
            if (t.getId().equals(paymentId)) {
                return t;
            }
        }
        return null;
    }

    private Long generateTransactionId(int lengthOfPaymentId) {
        double rndNum = Math.random();
        System.out.println(rndNum);
        long number = (long) (rndNum * Math.pow(10, lengthOfPaymentId));
        System.out.println("Generated Payment ID: " + number);
        return (number);
    }
}
