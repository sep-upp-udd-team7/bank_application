package com.project.bank1.service;

import com.project.bank1.dto.AcquirerResponseDto;
import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.dto.RequestDto;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;
import com.project.bank1.model.CreditCard;
import com.project.bank1.model.Transaction;
import com.project.bank1.repository.BankAccountRepository;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.CreditCardService;
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

    @Override
    public BankAccount addBankAccount(Client client) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAvailableFunds((double) 0);
        bankAccount.setReservedFunds((double) 0);
        bankAccount.setTransactions(new ArrayList<Transaction>());
        bankAccount.setCreditCard(creditCardService.addCreditCard(client.getName()));
        bankAccount.setClient(client);
        // TODO SD: generisanje ziro racuna
        bankAccount.setBankAccountNumber("123456789");
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }

    @Override
    public AcquirerResponseDto validateAcquirer(RequestDto dto) {
        if (!clientService.validateMerchantData(dto.getMerchantId(), dto.getMerchantPassword())) {
            return null;
        }
        AcquirerResponseDto response = new AcquirerResponseDto();
        response.setRequestDto(dto);
        // TODO: generiasati payment id
        response.setPaymentId(1L);
        System.out.println("********** url " + environment.getProperty("payment.url"));
        response.setPaymentUrl(environment.getProperty("payment.url"));
        return response;
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
