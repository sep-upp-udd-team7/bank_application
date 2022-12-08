package com.project.bank1.service;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.model.CreditCard;
import com.project.bank1.repository.CreditCardRepository;
import com.project.bank1.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardServiceImpl implements CreditCardService {
    private static Integer numberOfYearsOfCreditCardValidity = 5;
    private static Long numberOfDigitsThatDoesNotIdentifiesBank = 10000000000L;
    private static Long numberOfCcvDigitsThatDoesNotIdentifiesBank = 10000L;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private Environment environment;

    @Override
    public CreditCard addCreditCard(String clientsName) {
        CreditCard creditCard = new CreditCard();
        creditCard.setCardHolderName(clientsName);
        creditCard.setCvv(generateCvv());
        List<String> expDate = generateExperienceDate();
        creditCard.setMm(expDate.get(0));
        creditCard.setYy(expDate.get(1));
        creditCard.setPan(generatePan());
        creditCardRepository.save(creditCard);
        return creditCard;
    }

    @Override
    public CreditCard validateIssuerCreditCard(IssuerRequestDto dto) {
        for (CreditCard cc: creditCardRepository.findAll()) {
            if (cc.getPan().equals(dto.getPan()) && cc.getCardHolderName().equals(dto.getCardHolderName())
                && cc.getCvv().equals(dto.getCvv()) && cc.getMm().equals(dto.getMm()) && cc.getYy().equals(dto.getYy()) ) {
                if (checkExpirationDate(Integer.valueOf(dto.getMm()), Integer.valueOf(dto.getYy()))) {
                    System.out.println("Issuer's credit card has not expired");
                    return cc;
                } else {
                    System.out.println("Issuer's credit card has expired");
                    return null;
                }
            }
        }
        System.out.println("Issuer's credit card with the entered data was not found");
        return null;
    }

    private boolean checkExpirationDate(Integer mm, Integer yy) {
        LocalDateTime now = LocalDateTime.now();
        if (yy < now.getYear()) {
            return false;
        } else if (yy == now.getYear()) {
            if (mm < now.getMonthValue()) { // karica vazi do poslednjeg dana u mesecu (12/22 -> 31.12.2022.)
                return false;
            }
        }
        return true;
    }

    private String generatePan() {
        double rndNum = Math.random();
        System.out.println(rndNum);
        long last10 = (long) (rndNum * numberOfDigitsThatDoesNotIdentifiesBank);
        Long numberOfDigitsThatIdentifiesBank = Long.parseLong(environment.getProperty("bank.pan"));
        long number = numberOfDigitsThatIdentifiesBank * numberOfDigitsThatDoesNotIdentifiesBank + last10;
        System.out.println("Generated PAN: " + number);
        return String.valueOf(number);
    }

    private String generateCvv() {
        double rndNum = Math.random();
        System.out.println(rndNum);
        long cvv = (long) (rndNum * numberOfCcvDigitsThatDoesNotIdentifiesBank);
        System.out.println("Generated CVV:" + cvv);
        return String.valueOf(cvv);
    }

    private List<String> generateExperienceDate(){
        List<String> expDate = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String expMonth = String.valueOf(now.getMonthValue());
        String expYear = String.valueOf(now.getYear() + numberOfYearsOfCreditCardValidity);
        expDate.add(expMonth);
        expDate.add(expYear);
        return expDate;
    }

}
