package com.project.bank1.service;

import com.project.bank1.dto.AcquirerResponseDto;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.dto.ResponseDto;
import com.project.bank1.enums.TransactionStatus;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;
import com.project.bank1.model.CreditCard;
import com.project.bank1.model.Transaction;
import com.project.bank1.repository.BankAccountRepository;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.CreditCardService;
import com.project.bank1.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private Environment environment;
    @Autowired
    private TransactionService transactionService;

//    @Autowired(required = false)
//    private WebClient webClient;

    @Override
    public BankAccount addBankAccount(Client client) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAvailableFunds((double) 1000);
        bankAccount.setReservedFunds((double) 0);
        bankAccount.setTransactions(new ArrayList<Transaction>());
        bankAccount.setCreditCard(creditCardService.addCreditCard(client.getName()));
        bankAccount.setClient(client);
        bankAccount.setBankAccountNumber(generateBankAccountNumber(18));
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }

    private String generateBankAccountNumber(int length) { // ziro racun ima 18 cifara
        double rndNum = Math.random();
        long number = (long) (rndNum * Math.pow(10, length));
        System.out.println("Generated bank account number: " + number);
        return String.valueOf(number);
    }

    @Override
    public AcquirerResponseDto validateAcquirer(RequestDto dto) throws Exception {
        if (!clientService.validateMerchantData(dto.getMerchantId(), dto.getMerchantPassword())) {
            throw new Exception("Error while validating merchant credentials");
        }
        Transaction transaction = transactionService.createTransaction(dto);
        if (transaction == null) {
            throw new Exception("Error when creating acquirer's transaction");
        }
        String paymentUrl = environment.getProperty("bank.frontend.url") + environment
                .getProperty("bank.frontend.credit-card-data-module") + "/" + transaction.getId();

        AcquirerResponseDto response = new AcquirerResponseDto();
        response.setPaymentId(String.valueOf(transaction.getId()));
        response.setPaymentUrl(paymentUrl);
        transactionService.save(transaction);
        return response;
    }

    @Override
    public BankAccount findBankAccountByMerchantId(String merchantId) {
        for (BankAccount ba: bankAccountRepository.findAll()) {
            if (ba.getClient().getMerchantId().equals(merchantId)) {
                return ba;
            }
        }
        return null;
    }

    @Override
    public String validateIssuer(IssuerRequestDto dto) {
        if (!isIssuerInSameBankAsAcquirer(dto.getPan())) {
            // TODO: proslediti zahtev na PCC, za sad exception
            String msg = "Issuer and acquirer are not in the same bank! - PCC is implementing...";
            System.out.println(msg);
            return null;
        }

        Transaction transaction = transactionService.findByPaymentId(dto.getPaymentId());
        if (transaction == null) {
            String msg = "Transaction with id " + dto.getPaymentId() + " not found";
            System.out.println(msg);
            transaction.setStatus(TransactionStatus.ERROR);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
            //throw new Exception();
        }

        CreditCard issuerCreditCard = creditCardService.validateIssuerCreditCard(dto);
        if (issuerCreditCard == null) {
            String msg = "The issuer's credit card credentials are NOT correct or or the credit card has expired";
            System.out.println(msg);
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
            //throw new Exception(transaction.getErrorURL());
        }
        BankAccount issuerBankAccount = findIssuerBankAccountByCreditCardId(issuerCreditCard.getId());
        if (issuerBankAccount == null) {
            String msg = "Issuer bank account not found";
            System.out.println(msg);
            transaction.setStatus(TransactionStatus.ERROR);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
            //throw new Exception(transaction.getErrorURL());
        }
        transaction.setIssuerBankAccountId(issuerBankAccount.getId());

        if (issuerBankAccount.getAvailableFunds() < transaction.getAmount()) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            System.out.println("The customer's bank account does not have enough money");
            return finishPayment(transaction).getBody();
//            throw new FailedTransactionException("The customer's bank account does not have enough money", transaction.getFailedURL());
        }
        reserveFunds(issuerBankAccount, transaction.getAmount());

        // prebacivanje sredstava na racun prodavca
        BankAccount acquirerBankAccount = transaction.getBankAccount();
        Double newAvailableFundsAcquirer = acquirerBankAccount.getAvailableFunds() + transaction.getAmount();
        acquirerBankAccount.setAvailableFunds(newAvailableFundsAcquirer);

        issuerBankAccount.setReservedFunds(issuerBankAccount.getReservedFunds() - transaction.getAmount());
        bankAccountRepository.save(issuerBankAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionService.save(transaction);

        ResponseEntity<String> pspApplicationResponse = finishPayment(transaction);
        return pspApplicationResponse.getBody();
    }

    private ResponseEntity<String> finishPayment(Transaction transaction) {
        ResponseDto requestForPspApplication = getRequestDtoForPspApplication(transaction);

        ResponseEntity<String> pspApplicationResponse = WebClient.builder()
                .build().post()
                .uri(environment.getProperty("psp.finish-payment"))
                .body(BodyInserters.fromValue(requestForPspApplication))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();
        return pspApplicationResponse;
    }

    private ResponseDto getRequestDtoForPspApplication(Transaction transaction) {
        // get RequestDto when issuer and acquirer are in the SAME bank
        ResponseDto dto = new ResponseDto();
        dto.setPaymentId(transaction.getId());
        dto.setMerchantOrderId(transaction.getMerchantOrderId());
        dto.setTransactionStatus(transaction.getStatus().toString());
        dto.setAcquirerOrderId("");
        dto.setAcquirerTimestamp(null);
        return dto;
    }


    private void reserveFunds(BankAccount issuerBankAccount, Double amount) {
        Double newReservedFunds = issuerBankAccount.getReservedFunds() + amount;
        issuerBankAccount.setReservedFunds(newReservedFunds);
        Double newAvailableFunds = issuerBankAccount.getAvailableFunds() - amount;
        issuerBankAccount.setAvailableFunds(newAvailableFunds);
        bankAccountRepository.save(issuerBankAccount);
    }

    private BankAccount findIssuerBankAccountByCreditCardId(Long id) {
        for (BankAccount ba: bankAccountRepository.findAll()) {
            if (ba.getCreditCard().getId() == id) {
                return ba;
            }
        }
        return null;
    }

    private boolean isIssuerInSameBankAsAcquirer(String pan) {
        String issuersBankPan = pan.substring(0, 6);
        String acquirersBankPan = environment.getProperty("bank.pan");
        return issuersBankPan.equals(acquirersBankPan);
    }

}
