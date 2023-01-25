package com.project.bank1.service;

import com.project.bank1.dto.PccRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.enums.TransactionStatus;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.CreditCard;
import com.project.bank1.model.Transaction;
import com.project.bank1.repository.TransactionRepository;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.CreditCardService;
import com.project.bank1.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static int issuerOrderId = 10;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private ClientService clientService;

    @Override
    public Transaction createTransaction(RequestDto dto) {
        System.out.println("Create transaction.....");
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

    @Override
    public void updateStatus(String id, TransactionStatus failed) {
        Transaction t = transactionRepository.findById(id).get();
        if (t != null) {
            t.setStatus(failed);
            transactionRepository.save(t);
        }
    }

    private Long generateTransactionId(int lengthOfPaymentId) {
        double rndNum = Math.random();
        System.out.println(rndNum);
        long number = (long) (rndNum * Math.pow(10, lengthOfPaymentId));
        System.out.println("Generated Payment ID: " + number);
        return (number);
    }

    public Transaction createTransactionForIssuer(PccRequestDto dto) {
        System.out.println("Create transaction for issuer........");

        CreditCard cc;
        if(dto.getIssuer() == null){
            System.out.println("Credit card payment....");
            cc = creditCardService.findByPan(dto.getPan());
        }
        else{
            System.out.println("Qr code payment....");
            cc = clientService.getByEmail(dto.getIssuer()).getBankAccount().getCreditCard();
        }

        BankAccount issuerBankAccount = bankAccountService.findBankAccountByCreditCardId(cc.getId());

        if (issuerBankAccount == null) {
            System.out.println("Issuer bank account not found!");
            return null;
        }
        Transaction transaction = new Transaction();
        // TODO: ispraviti generisanje transaction id tj. payment id
        transaction.setId(String.valueOf(generateTransactionId(10)));
        transaction.setBankAccount(issuerBankAccount);
        transaction.setAmount(dto.getAmount());
        transaction.setErrorURL(dto.getErrorURL());
        transaction.setFailedURL(dto.getFailedURL());
        transaction.setSuccessURL(dto.getSuccessURL());
        transaction.setMerchantOrderId(dto.getMerchantOrderId());
        transaction.setMerchantTimestamp(dto.getMerchantTimestamp());
        transaction.setStatus(TransactionStatus.CREATED);
        transaction.setAcquirerOrderId(dto.getAcquirerOrderId());
        transaction.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        transaction.setStatus(TransactionStatus.CREATED);

        //generisanje issuerOrderId-ja i issuerOrderTimestamp-a
        String ioId = generateRandomString(issuerOrderId);
        LocalDateTime issuerTimestamp = LocalDateTime.now();
        transaction.setIssuerOrderId(ioId);
        transaction.setIssuerTimestamp(issuerTimestamp);

        transactionRepository.save(transaction);
        return transaction;
    }


    private String generateRandomString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println(generatedString);
        return generatedString;
    }
}
