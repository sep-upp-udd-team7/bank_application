package com.project.bank1.service;

import com.project.bank1.dto.IssuerRequestDto;
import com.project.bank1.model.CreditCard;
import com.project.bank1.repository.CreditCardRepository;
import com.project.bank1.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardServiceImpl implements CreditCardService {
    private LoggerService loggerService = new LoggerService(this.getClass());
    private static Integer numberOfYearsOfCreditCardValidity = 5;
    private static Long numberOfDigitsThatDoesNotIdentifiesBank = 10000000000L;
    private static Long numberOfCcvDigitsThatDoesNotIdentifiesBank = 10000L;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private Environment environment;

    @Override
    public CreditCard addCreditCard(String clientsName) {
        loggerService.infoLog(MessageFormat.format("Creating credit card for client with name: {0}", clientsName));
        CreditCard creditCard = new CreditCard();
        creditCard.setCardHolderName(clientsName);
        creditCard.setCvv(generateCvv());
        List<String> expDate = generateExperienceDate();
        creditCard.setMm(expDate.get(0));
        creditCard.setYy(expDate.get(1));
        creditCard.setPan(generatePan());
        creditCardRepository.save(creditCard);
        loggerService.successLog(MessageFormat.format("Credit card created for client: {0}", clientsName));
        return creditCard;
    }

    @Override
    public CreditCard validateIssuerCreditCard(IssuerRequestDto dto) {
        for (CreditCard cc: creditCardRepository.findAll()) {
            if (cc.getPan().equals(dto.getPan()) && cc.getCardHolderName().equals(dto.getCardHolderName())
                && cc.getCvv().equals(dto.getCvv()) && cc.getMm().equals(dto.getMm()) && cc.getYy().equals(dto.getYy()) ) {
                if (checkExpirationDate(Integer.valueOf(dto.getMm()), Integer.valueOf(dto.getYy()))) {
                    loggerService.debugLog("Issuer's credit card has not expired");
                    return cc;
                } else {
                    loggerService.errorLog("Issuer's credit card has expired");
                    return null;
                }
            }
        }
        loggerService.errorLog("Issuer's credit card with the entered data was not found");
        return null;
    }



    private boolean checkExpirationDate(Integer mm, Integer yy) {
        loggerService.infoLog("Checking credit card's expiration date");
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
        loggerService.infoLog("Generation PAN number");
        double rndNum = Math.random();
        System.out.println(rndNum);
        long last10 = (long) (rndNum * numberOfDigitsThatDoesNotIdentifiesBank);
        Long numberOfDigitsThatIdentifiesBank = Long.parseLong(environment.getProperty("bank.pan"));
        long number = numberOfDigitsThatIdentifiesBank * numberOfDigitsThatDoesNotIdentifiesBank + last10;
        return String.valueOf(number);
    }

    private String generateCvv() {
        loggerService.infoLog("Generating CVC/CVV");
        double rndNum = Math.random();
        System.out.println(rndNum);
        long cvv = (long) (rndNum * numberOfCcvDigitsThatDoesNotIdentifiesBank);
        return String.valueOf(cvv);
    }

    private List<String> generateExperienceDate(){
        loggerService.debugLog(MessageFormat.format("Client's credit card is valid for {0} years", numberOfYearsOfCreditCardValidity));
        List<String> expDate = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String expMonth = String.valueOf(now.getMonthValue());
        String expYear = String.valueOf(now.getYear() + numberOfYearsOfCreditCardValidity);
        expDate.add(expMonth);
        expDate.add(expYear);
        return expDate;
    }


    @Override
    public CreditCard findByPan(String pan) {
        loggerService.infoLog("Finding credit card by PAN number");
        for(CreditCard cc: creditCardRepository.findAll()){
            if(cc.getPan().equals(pan)){
                loggerService.successLog(MessageFormat.format("Credit card found by PAN number with ID: {0}", cc.getId()));
                return cc;
            }
        }
        loggerService.errorLog("Credit card not found by PAN number");
        return null;
    }

}
