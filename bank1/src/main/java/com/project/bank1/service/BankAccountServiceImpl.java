package com.project.bank1.service;

import com.project.bank1.dto.AcquirerResponseDto;
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



        return null;
    }


}
