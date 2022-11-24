package com.project.bank1.service;

import com.project.bank1.model.CreditCard;
import com.project.bank1.repository.CreditCardRepository;
import com.project.bank1.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Service;

@Service
public class CreditCardServiceImpl implements CreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Override
    public CreditCard addCreditCard(String clientsName) {
        CreditCard creditCard = new CreditCard();
        // TODO SD: popunjavanje polja na kartici
        creditCard.setCardHolderName(clientsName);
        creditCard.setCvv("334");
        creditCard.setMm("12");
        creditCard.setYy("24");
        creditCard.setPan("1234123412341234");
        creditCardRepository.save(creditCard);
        return creditCard;
    }
}
