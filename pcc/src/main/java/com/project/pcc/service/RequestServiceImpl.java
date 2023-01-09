package com.project.pcc.service;

import com.project.pcc.dto.PccRequestDto;
import com.project.pcc.dto.PccResponseDto;
import com.project.pcc.enums.TransactionStatus;
import com.project.pcc.model.Bank;
import com.project.pcc.model.Request;
import com.project.pcc.repository.BankRepository;
import com.project.pcc.repository.RequestRepository;
import com.project.pcc.service.interfaces.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private Environment environment;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BankRepository bankRepository;

    @Override
    public PccResponseDto checkIsValidAndRedirect(PccRequestDto dto) {

        String msg = "Starting with PCC...";
        System.out.println(msg);

        Request r = createNewRequest(dto);

        if(!checkIsValidRequest(dto)){
            msg = "Request in not valid or pan doesn't exist!";
            System.out.println(msg);
            r.setStatus(TransactionStatus.ERROR); //TODO: provjeri da li je error ili failed
            requestRepository.save(r);
            //u slucaju neispravnog zahtjeva, vraca se na banku 1
            return createPccResponse(dto);
        }

        //zahtjev se salje na banku 2 i odgovor se vraca banci1
        msg = "Sending request to Bank 2...";
        System.out.println(msg);

        PccResponseDto response = sendRequestToIssuerBank(dto).getBody();
        if(response == null){
            System.out.println("Resposne from bank 2 is null....");
            return createPccResponse(dto);
        }
        System.out.println("Resposne from bank 2 is not null....");
        r.setStatus(findStatus(response.getTransactionStatus()));
        r.setIssuerOrderId(response.getIssuerOrderId());
        r.setIssuerTimestamp(response.getIssuerTimestamp());
        requestRepository.save(r);

        return response;
    }

    private TransactionStatus findStatus(String status){
        if(status.equals(TransactionStatus.SUCCESS.toString())){
            return TransactionStatus.SUCCESS;
        }
        else if(status.equals(TransactionStatus.FAILED.toString())){
            return TransactionStatus.FAILED;
        }
        else if(status.equals(TransactionStatus.ERROR.toString())){
            return TransactionStatus.ERROR;
        }
        else if(status.equals(TransactionStatus.CREATED.toString())){
            return TransactionStatus.CREATED;
        }

        return null;
    }

    Boolean checkIsValidRequest(PccRequestDto dto){
        System.out.println(dto.getAmount());

        if(dto.getAcquirerOrderId() == null){
            return false;
        }
        /*else if(dto.getAcquirerTimestamp() == null || dto.getAcquirerTimestamp().isAfter(LocalDateTime.now())){
            return false;
        }
        else if(dto.getAmount() == null || dto.getAmount() <= 0){
            return false;
        }
        else if(dto.getMerchantOrderId() == null){
            return false;
        }
        else if(dto.getMerchantTimestamp() == null || dto.getMerchantTimestamp().isAfter(LocalDateTime.now())){
            return false;
        }
        else if(dto.getSuccessURL() == null){
            return false;
        }
        else if(dto.getFailedURL() == null){
            return false;
        }
        else if(dto.getErrorURL() == null) {
            return false;
        }*/
        System.out.println("*********************************");
        System.out.println("PAN is:" + dto.getPan());
        System.out.println("Bank name is: " + dto.getBankName());
        //provjera da li banka
        if(dto.getPan() != null){
            System.out.println("PAN is not null, finding bank");
            System.out.println("PAN is: " + dto.getPan());
            for(Bank b: bankRepository.findAll()){
                if(dto.getPan().substring(0,6).equals(b.getPan())){
                    return true;
                }
            }
        }
        else{
            System.out.println("PAN is null, finding bank by name: " + dto.getBankName());
            for(Bank b: bankRepository.findAll()){
                if(b.getName().equals(dto.getBankName())){
                    return true;
                }
            }
        }

        return false;
    }


    private ResponseEntity<PccResponseDto> sendRequestToIssuerBank(PccRequestDto requestForIssuerBank) {

        String url = findBankUrlToRedirect(requestForIssuerBank);

        System.out.println("na ovaj url se salje:" + url);

        ResponseEntity<PccResponseDto> issuerBankResponse = WebClient.builder()
                .build().post()
                .uri(url)
                .body(BodyInserters.fromValue(requestForIssuerBank))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(PccResponseDto.class)
                .block();
        return issuerBankResponse;
    }

    Request createNewRequest(PccRequestDto dto){
        Request r = new Request();
        r.setAcquirerOrderId(dto.getAcquirerOrderId());
        r.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        r.setAmount(dto.getAmount());
        r.setMerchantOrderId(dto.getMerchantOrderId());
        r.setMerchantTimestamp(dto.getMerchantTimestamp());
        r.setSuccessURL(dto.getSuccessURL());
        r.setFailedURL(dto.getFailedURL());
        r.setErrorURL(dto.getErrorURL());
        r.setStatus(TransactionStatus.CREATED);
        r.setQrCodePayment(dto.getQrCodePayment());
        /*if(dto.getPan() != null){
            r.setPan(dto.getPan());
        }
        if(dto.getCvv() != null){
            r.setCvv(dto.getCvv());
        }
        if(dto.getMm() != null){
            r.setMm(dto.getMm());
        }
        if(dto.getYy() != null){
            r.setYy(dto.getYy());
        }*/
        if(dto.getCardHolderName() != null){
            r.setIssuer(dto.getCardHolderName());
        }
        else if(dto.getIssuer() != null){
            r.setIssuer(dto.getIssuer());
        }


        return requestRepository.save(r);
    }

    PccResponseDto createPccResponse(PccRequestDto dto){
        PccResponseDto res = new PccResponseDto();
        res.setAcquirerOrderId(dto.getAcquirerOrderId());
        res.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        res.setTransactionStatus("ERROR");

        return res;
    }

    String findBankUrlToRedirect(PccRequestDto requestForIssuerBank){
        System.out.println("Finding bank 2 url.....");
        for(Bank b: bankRepository.findAll()){
            System.out.println(b.getPan());
            if(requestForIssuerBank.getPan() != null){
                System.out.println("PAN != null");
                System.out.println(requestForIssuerBank.getPan().substring(0, 6));
                if(b.getPan().equals(requestForIssuerBank.getPan().substring(0, 6))){
                    System.out.println("Credit card payment.....");
                    return b.getUrl();
                }
            }else{
                if(requestForIssuerBank.getBankName().equals(b.getName())){
                    System.out.println("Qr code payment.....");
                    return b.getUrl();
                }
            }

        }
        System.out.println("Bank url not found....");
        return "";
    }

}
