package com.project.bank1.service;

import com.project.bank1.dto.*;
import com.project.bank1.enums.TransactionStatus;
import com.project.bank1.model.BankAccount;
import com.project.bank1.model.Client;
import com.project.bank1.model.CreditCard;
import com.project.bank1.model.Transaction;
import com.project.bank1.repository.BankAccountRepository;
import com.project.bank1.repository.ClientRepository;
import com.project.bank1.service.interfaces.BankAccountService;
import com.project.bank1.service.interfaces.ClientService;
import com.project.bank1.service.interfaces.CreditCardService;
import com.project.bank1.service.interfaces.TransactionService;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private LoggerService loggerService = new LoggerService(this.getClass());
    private static int acquirerOrderId = 10;

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
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public BankAccount addBankAccount(Client client) {
        loggerService.infoLog(MessageFormat.format("Creating bank account for client with name: {0}", client.getName()));
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAvailableFunds((double) 1000);
        bankAccount.setReservedFunds((double) 0);
        bankAccount.setTransactions(new ArrayList<Transaction>());
        bankAccount.setCreditCard(creditCardService.addCreditCard(client.getName()));
        bankAccount.setClient(client);
        bankAccount.setBankAccountNumber(generateBankAccountNumber(18));
        bankAccountRepository.save(bankAccount);
        loggerService.successLog(MessageFormat.format("Bank account created for client with name: {0}", client.getName()));
        return bankAccount;
    }

    private String generateBankAccountNumber(int length) { // ziro racun ima 18 cifara
        loggerService.infoLog(MessageFormat.format("Generating bank account number of length with {0} characters", length));
        double rndNum = Math.random();
        long number = (long) (rndNum * Math.pow(10, length));
        return String.valueOf(number);
    }

    @Override
    public AcquirerResponseDto validateAcquirer(RequestDto dto) throws Exception {
        loggerService.infoLog(MessageFormat.format("Starting acquirer validation with merchant order ID: {0}", dto.getMerchantOrderId()));
        if (!clientService.validateMerchantData(dto.getMerchantId(), dto.getMerchantPassword())) {
            loggerService.errorLog(MessageFormat.format("Incorrect merchant credentials or client with merchant ID: {0} does not exist", dto.getMerchantId()));
            throw new Exception("Error while validating merchant credentials");
        }

        Transaction transaction = transactionService.createTransaction(dto);
        if (transaction == null) {
            loggerService.errorLog("Acquirer's transaction is not created");
            throw new Exception("Error when creating acquirer's transaction");
        }
        loggerService.debugLog(MessageFormat.format("Created acquirer's transaction for paying order with ID: {0}", transaction.getMerchantOrderId()));

        //TODO:u zavisnosti da li je qr code placanje ili kartica promijeniti url
        AcquirerResponseDto response = new AcquirerResponseDto();
        response.setPaymentId(String.valueOf(transaction.getId()));
        String paymentUrl = "";
        if(!dto.getQrCode()){
            loggerService.debugLog(MessageFormat.format("Payment option is credit card for order with ID: {0}", dto.getMerchantOrderId()));
            paymentUrl = environment.getProperty("bank.frontend.url") + environment
                    .getProperty("bank.frontend.credit-card-data-module") + "/" + transaction.getId();
        }
        else{
            loggerService.debugLog(MessageFormat.format("Payment option is QR code for order with ID: {0}", dto.getMerchantOrderId()));
            paymentUrl = environment.getProperty("bank.frontend.url") + environment
                    .getProperty("bank.frontend.qr-code") + "/" + transaction.getId();
        }
        response.setAmount(dto.getAmount());
        response.setAcquirer(transaction.getBankAccount().getClient().getName());
        response.setAcquirerBankAccount(transaction.getBankAccount().getBankAccountNumber());
        response.setPaymentUrl(paymentUrl);
        transactionService.save(transaction);
        loggerService.successLog(MessageFormat.format("Successful acquirer validation for order with ID: {0}", dto.getMerchantOrderId()));
        return response;
    }

    @Override
    public BankAccount findBankAccountByMerchantId(String merchantId) {
        loggerService.infoLog(MessageFormat.format("Finding bank account by merchant ID: {0}", merchantId));
        for (BankAccount ba: bankAccountRepository.findAll()) {
            if (ba.getClient().getMerchantId() != null) {
                if (ba.getClient().getMerchantId().equals(merchantId)) {
                    loggerService.successLog(MessageFormat.format("Found bank account with ID: {0}", ba.getId()));
                    return ba;
                }
            }
        }
        loggerService.warnLog(MessageFormat.format("Bank account not found by merchant ID: {0}", merchantId));
        return null;
    }

    @Override
    public String validateIssuer(IssuerRequestDto dto) {
        loggerService.infoLog(MessageFormat.format("Starting issuer validation for payment with ID: {0}", dto.getPaymentId()));
        if(dto.getIssuer() != null && dto.getBankName() != null){
            loggerService.debugLog("QR code payment option is selected");
            Client c = clientService.getByEmail(dto.getIssuer());

            if (c != null && environment.getProperty("bank.name").equals(dto.getBankName())) {
                loggerService.debugLog("Issuer and acquirer are in the same bank");
                dto.setCardHolderName(c.getName());
                dto.setPan(c.getBankAccount().getCreditCard().getPan());
                dto.setCvv(c.getBankAccount().getCreditCard().getCvv());
                dto.setYy(c.getBankAccount().getCreditCard().getYy());
                dto.setMm(c.getBankAccount().getCreditCard().getMm());
            }
        }

        if (!isIssuerInSameBankAsAcquirer(dto.getPan())) {
            loggerService.debugLog("Issuer and acquirer are not in the same bank");
            PccResponseDto pccResponse = createAndSendPccRequest(dto);
            loggerService.debugLog(MessageFormat.format("Request sent to PCC for execution payment with ID: {0}", dto.getPaymentId()));

            Transaction transaction = transactionService.findByPaymentId(dto.getPaymentId());
            if (transaction == null) {
                loggerService.errorLog(MessageFormat.format("Transaction with ID: {0} not found", dto.getPaymentId()));
                transaction.setStatus(TransactionStatus.ERROR);
                transactionService.save(transaction);
                return finishPayment(transaction).getBody();
            }
            transaction.setAcquirerOrderId(pccResponse.getAcquirerOrderId());
            transaction.setAcquirerTimestamp(pccResponse.getAcquirerTimestamp());
            transaction.setIssuerTimestamp(pccResponse.getIssuerTimestamp());
            transaction.setIssuerOrderId(pccResponse.getIssuerOrderId());
            transaction.setIssuerBankAccountId(pccResponse.getIssuerBankAccountId());
            loggerService.debugLog(MessageFormat.format("Supplementing data on a transaction that has ID: {0}", transaction.getId()));

            if(pccResponse.getTransactionStatus().equals(TransactionStatus.FAILED.toString())){
                loggerService.errorLog("Something was FAILED in issuer bank");
                transaction.setStatus(TransactionStatus.FAILED);
                transactionService.save(transaction);
                return finishPayment(transaction).getBody();
            }
            else if(pccResponse.getTransactionStatus().equals(TransactionStatus.ERROR.toString())){
                loggerService.errorLog("There was an ERROR in issuer bank");
                transaction.setStatus(TransactionStatus.ERROR);
                transactionService.save(transaction);
                return finishPayment(transaction).getBody();
            }
            loggerService.debugLog("Transfer of money to the issuer's account");
            BankAccount acquirerBankAccount = transaction.getBankAccount();
            Double newAvailableFundsAcquirer = acquirerBankAccount.getAvailableFunds() + transaction.getAmount();
            acquirerBankAccount.setAvailableFunds(newAvailableFundsAcquirer);
            //bankAccountRepository.save(acquirerBankAccount);

            //TODO: poslati mozda payment id i azurirati transakciju a ne dodati novu
            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
        }

        loggerService.debugLog("Issuer and acquirer are in the same bank");
        Transaction transaction = transactionService.findByPaymentId(dto.getPaymentId());
        if (transaction == null) {
            loggerService.errorLog(MessageFormat.format("Transaction with ID: {0} not found", dto.getPaymentId()));
            transaction.setStatus(TransactionStatus.ERROR);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
        }

        CreditCard issuerCreditCard = creditCardService.validateIssuerCreditCard(dto);
        if (issuerCreditCard == null) {
            loggerService.errorLog("The issuer's credit card credentials are incorrect or or the credit card has expired");
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
        }
        BankAccount issuerBankAccount = findIssuerBankAccountByCreditCardId(issuerCreditCard.getId());
        if (issuerBankAccount == null) {
            loggerService.errorLog("Issuer bank account not found by credit card ID");
            transaction.setStatus(TransactionStatus.ERROR);
            transactionService.save(transaction);
            return finishPayment(transaction).getBody();
        }
        transaction.setIssuerBankAccountId(issuerBankAccount.getId());


        if (issuerBankAccount.getAvailableFunds() < transaction.getAmount()) {
            loggerService.errorLog("The issuer's bank account does not have enough money");
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            System.out.println();
            return finishPayment(transaction).getBody();
        }
        loggerService.debugLog(MessageFormat.format("Reserving funds by issuer: {0} and amount: {1}", issuerCreditCard.getCardHolderName(), transaction.getAmount()));
        reserveFunds(issuerBankAccount, transaction.getAmount());

        BankAccount acquirerBankAccount = transaction.getBankAccount();
        loggerService.debugLog(MessageFormat.format("Money transfer from issuer bank ID: {0} to acquirer bank ID: {1}", issuerBankAccount.getId(), acquirerBankAccount.getId()));
        Double newAvailableFundsAcquirer = acquirerBankAccount.getAvailableFunds() + transaction.getAmount();
        acquirerBankAccount.setAvailableFunds(newAvailableFundsAcquirer);

        loggerService.debugLog(MessageFormat.format("Removing reserved funds from issuer: {0} and amount: {1}", issuerCreditCard.getCardHolderName(), transaction.getAmount()));
        issuerBankAccount.setReservedFunds(issuerBankAccount.getReservedFunds() - transaction.getAmount());
        bankAccountRepository.save(issuerBankAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionService.save(transaction);

        ResponseEntity<String> pspApplicationResponse = finishPayment(transaction);
        return pspApplicationResponse.getBody();

    }

    private ResponseEntity<String> finishPayment(Transaction transaction) {
        loggerService.debugLog("Sending request to bank microservice in PSP for finishing payment transaction");
        ResponseDto requestForPspApplication = getRequestDtoForPspApplication(transaction);
        ResponseEntity<String> pspApplicationResponse = WebClient.builder()
                .build().post()
                .uri(environment.getProperty("psp.finish-payment"))
                .body(BodyInserters.fromValue(requestForPspApplication))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();
        loggerService.successLog(MessageFormat.format("Payment finished by transaction with ID: {0}, redirection to: {1}", transaction.getId(), pspApplicationResponse));
        return pspApplicationResponse;
    }



    private ResponseDto getRequestDtoForPspApplication(Transaction transaction) {
        loggerService.debugLog("Creating body for request");
        ResponseDto dto = new ResponseDto();
        dto.setPaymentId(transaction.getId());
        dto.setMerchantOrderId(transaction.getMerchantOrderId());
        dto.setTransactionStatus(transaction.getStatus().toString());
        if(transaction.getAcquirerOrderId() == null && transaction.getAcquirerTimestamp() == null) {
            dto.setAcquirerOrderId("");
            dto.setAcquirerTimestamp(null);
        } else {
            dto.setAcquirerOrderId(transaction.getAcquirerOrderId());
            dto.setAcquirerTimestamp(transaction.getAcquirerTimestamp());
        }
        return dto;
    }


    private void reserveFunds(BankAccount issuerBankAccount, Double amount) {
        loggerService.infoLog("Reserving funds by bank account with ID: " + issuerBankAccount.getId());
        Double newReservedFunds = issuerBankAccount.getReservedFunds() + amount;
        issuerBankAccount.setReservedFunds(newReservedFunds);
        Double newAvailableFunds = issuerBankAccount.getAvailableFunds() - amount;
        issuerBankAccount.setAvailableFunds(newAvailableFunds);
        bankAccountRepository.save(issuerBankAccount);
    }

    private BankAccount findIssuerBankAccountByCreditCardId(Long id) {
        loggerService.debugLog("Finding bank account by credit card ID: " + id);
        for (BankAccount ba: bankAccountRepository.findAll()) {
            if (ba.getCreditCard().getId() == id) {
                return ba;
            }
        }
        return null;
    }

    private boolean isIssuerInSameBankAsAcquirer(String pan) {
        if(!pan.equals("")){
            String issuersBankPan = pan.substring(0, 6);
            String acquirersBankPan = environment.getProperty("bank.pan");
            return issuersBankPan.equals(acquirersBankPan);
        }
        return false;
    }


    private String generateRandomString(int targetStringLength) {
        loggerService.infoLog(MessageFormat.format("Generating random string with a length of {0} characters", targetStringLength));
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }


    private PccRequestDto createPccRequest(IssuerRequestDto dto){
        Transaction transaction = transactionService.findByPaymentId(dto.getPaymentId());
;       loggerService.infoLog("Creating PCC request");

        PccRequestDto pccRequest = new PccRequestDto();
        pccRequest.setAcquirerOrderId(generateRandomString(acquirerOrderId));
        pccRequest.setAcquirerTimestamp(LocalDateTime.now());
        pccRequest.setAmount(transaction.getAmount());
        pccRequest.setMerchantOrderId(transaction.getMerchantOrderId());
        pccRequest.setMerchantTimestamp(transaction.getMerchantTimestamp());
        pccRequest.setSuccessURL(transaction.getSuccessURL());
        pccRequest.setFailedURL(transaction.getFailedURL());
        pccRequest.setErrorURL(transaction.getErrorURL());
        if(dto.getIssuer() != null && dto.getBankName() != null){
            pccRequest.setQrCodePayment(true);
            pccRequest.setBankName(dto.getBankName());
            pccRequest.setIssuer(dto.getIssuer());
        }else{
            pccRequest.setCvv(dto.getCvv());
            pccRequest.setPan(dto.getPan());
            pccRequest.setMm(dto.getMm());
            pccRequest.setYy(dto.getYy());
            pccRequest.setCardHolderName(dto.getCardHolderName());
        }
        return pccRequest;
    }



    private ResponseEntity<PccResponseDto> sendRequestToPcc(PccRequestDto requestForPccApplication) {
        ResponseEntity<PccResponseDto> pccApplicationResponse = WebClient.builder()
                .build().post()
                .uri(environment.getProperty("pcc.pcc-request"))
                .body(BodyInserters.fromValue(requestForPccApplication))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(PccResponseDto.class)
                .block();
        if (pccApplicationResponse.getStatusCode().is2xxSuccessful()) {
            loggerService.successLog("Successfully received response from PCC");
        }
        return pccApplicationResponse;
    }

    @Override
    public PccResponseDto issuerPaymentDifferentBanks(PccRequestDto dto) {
        String msgs = "Payment when acquirer != issuer started!";
        System.out.println(msgs);

        Transaction transaction = transactionService.createTransactionForIssuer(dto);
        if(dto.getIssuer() != null && dto.getBankName() != null){
            System.out.println("issuerPaymentDifferentBanks qr code payment...");
            BankAccount issuerBankAccount = findIssuerBankAccountByIssuerEmail(dto.getIssuer());

            if(validQrCodePayment(dto, issuerBankAccount)){
                System.out.println("QR code payment is valid...");
                transaction.setIssuerBankAccountId(issuerBankAccount.getId());
                transaction.setStatus(TransactionStatus.SUCCESS);
            }
            else {
                transaction.setStatus(TransactionStatus.FAILED);
            }
            transactionService.save(transaction);
            return createResponseToPcc(transaction);
        }

        System.out.println("issuerPaymentDifferentBanks credit card payment...");
        IssuerRequestDto issuerRequestDto = createIssuerRequestDto(dto);
        CreditCard issuerCreditCard = creditCardService.validateIssuerCreditCard(issuerRequestDto);
        if (issuerCreditCard == null) {
            String msg = "The issuer's credit card credentials are NOT correct or the credit card has expired";
            System.out.println(msg);
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            return createResponseToPcc(transaction);
        }

        BankAccount issuerBankAccount = findIssuerBankAccountByCreditCardId(issuerCreditCard.getId());
        if (issuerBankAccount == null) {
            String msg = "Issuer bank account not found";
            System.out.println(msg);
            transaction.setStatus(TransactionStatus.ERROR);
            transactionService.save(transaction);
            return createResponseToPcc(transaction);
        }
        transaction.setIssuerBankAccountId(issuerBankAccount.getId());

        if (issuerBankAccount.getAvailableFunds() < transaction.getAmount()) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionService.save(transaction);
            System.out.println("The customer's bank account does not have enough money");
            return createResponseToPcc(transaction);
        }
        reserveFunds(issuerBankAccount, transaction.getAmount());
        //smanji rezervisana sredstva
        issuerBankAccount.setReservedFunds(issuerBankAccount.getReservedFunds() - transaction.getAmount());
        bankAccountRepository.save(issuerBankAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionService.save(transaction);

        return createResponseToPcc(transaction);
    }

    @Override
    public BankAccount findBankAccountByCreditCardId(Long creditCardId) {
        for(BankAccount ba: bankAccountRepository.findAll()){
            if(ba.getCreditCard().getId().equals(creditCardId)){
                return ba;
            }
        }
        return null;
    }

    private IssuerRequestDto createIssuerRequestDto(PccRequestDto dto){
        IssuerRequestDto ir = new IssuerRequestDto();
        ir.setCardHolderName(dto.getCardHolderName());
        ir.setPan(dto.getPan());
        ir.setMm(dto.getMm());
        ir.setYy(dto.getYy());
        ir.setCvv(dto.getCvv());

        return ir;
    }


    private PccResponseDto createResponseToPcc(Transaction transaction){
        PccResponseDto pccResponseDto = new PccResponseDto();
        pccResponseDto.setTransactionStatus(transaction.getStatus().toString());
        pccResponseDto.setAcquirerOrderId(transaction.getAcquirerOrderId());
        pccResponseDto.setAcquirerTimestamp(transaction.getAcquirerTimestamp());
        pccResponseDto.setIssuerOrderId(transaction.getIssuerOrderId());
        pccResponseDto.setIssuerTimestamp(transaction.getIssuerTimestamp());
        pccResponseDto.setIssuerBankAccountId(transaction.getIssuerBankAccountId());

        return  pccResponseDto;
    }

    private PccResponseDto createAndSendPccRequest(IssuerRequestDto dto){
        PccRequestDto pccRequest =  createPccRequest(dto);
        return sendRequestToPcc(pccRequest).getBody();
    }

    private Boolean validQrCodePayment(PccRequestDto dto, BankAccount issuerBankAccount){
        if (issuerBankAccount == null) {
            String msg = "Issuer bank account not found";
            System.out.println(msg);
            return false;
        }

        if (issuerBankAccount.getAvailableFunds() < dto.getAmount()) {
            System.out.println("The customer's bank account does not have enough money");
            return false;
        }
        reserveFunds(issuerBankAccount, dto.getAmount());
        issuerBankAccount.setReservedFunds(issuerBankAccount.getReservedFunds() - dto.getAmount());
        bankAccountRepository.save(issuerBankAccount);

        return true;
    }

    private BankAccount findIssuerBankAccountByIssuerEmail(String email) {
        for(Client c: clientRepository.findAll()){
            if(c.getEmail().equals(email)){
                return c.getBankAccount();
            }
        }
        return null;
    }

}
