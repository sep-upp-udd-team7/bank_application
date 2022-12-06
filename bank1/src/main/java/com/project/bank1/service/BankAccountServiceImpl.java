package com.project.bank1.service;

import com.project.bank1.dto.AcquirerResponseDto;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
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
import org.springframework.stereotype.Service;

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

    @Override
    public BankAccount addBankAccount(Client client) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAvailableFunds((double) 0);
        bankAccount.setReservedFunds((double) 0);
        bankAccount.setTransactions(new ArrayList<Transaction>());
        bankAccount.setCreditCard(creditCardService.addCreditCard(client.getName()));
        bankAccount.setClient(client);
        bankAccount.setBankAccountNumber(generateBankAccountNumber(18));
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }

    private String generateBankAccountNumber(int length) {
        // SD: nasla sam na netu da ziro racun ima 18 cifara
        double rndNum = Math.random();
        long number = (long) (rndNum * Math.pow(10, length));
        System.out.println("Generated bank account number: " + number);
        return String.valueOf(number);
    }

    @Override
    public AcquirerResponseDto validateAcquirer(RequestDto dto) {
        if (!clientService.validateMerchantData(dto.getMerchantId(), dto.getMerchantPassword())) {
            return null;
        }
        Transaction transaction = transactionService.createAcquirerTransaction(dto);
        String paymentUrl = environment.getProperty("bank.frontend.url") + environment
                .getProperty("bank.frontend.credit-card-data-module") + "/" + transaction.getId();
        System.out.println(" ********** URL - " + paymentUrl);

        AcquirerResponseDto response = new AcquirerResponseDto();
        response.setPaymentId(String.valueOf(transaction.getId()));
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    @Override
    public BankAccount findBankAccountByMerchantId(String merchantId) {
        for (BankAccount bankAccount: bankAccountRepository.findAll()) {
            if (bankAccount.getClient().getMerchantId().equals(merchantId)) {
                return bankAccount;
            }
        }
        return null;
    }

    @Override
    public Object validateIssuer(IssuerRequestDto dto) {
        // TODO: Object???
        if (!creditCardService.validateIssuerCreditCard(dto)) {
            System.out.println("The issuer's credit card credentials are NOT correct");
            return null;
        }
        return null;
    }
}
