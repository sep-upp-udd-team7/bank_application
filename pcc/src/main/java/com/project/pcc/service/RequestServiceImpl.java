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
            return createPccResponse(dto);
        }

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
        else if(dto.getAcquirerTimestamp() == null || dto.getAcquirerTimestamp().isAfter(LocalDateTime.now())){
            return false;
        }
        else if(dto.getCvv() == null){
            return false;
        }
        else if(dto.getCardHolderName() == null){
            return false;
        }
        else if(dto.getPan() == null){
            return false;
        }
        else if(dto.getMm() == null){
            return false;
        }
        else if(dto.getYy() == null){
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
        }
        //provjera da li postoji pan broj
        for(Bank b: bankRepository.findAll()){
            if(dto.getPan().substring(0,6).equals(b.getPan())){
                return true;
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
        r.setCardHolderName(dto.getCardHolderName());
        r.setCvv(dto.getCvv());
        r.setPan(dto.getPan());
        r.setMm(dto.getMm());
        r.setYy(dto.getYy());
        r.setAmount(dto.getAmount());
        r.setMerchantOrderId(dto.getMerchantOrderId());
        r.setMerchantTimestamp(dto.getMerchantTimestamp());
        r.setSuccessURL(dto.getSuccessURL());
        r.setFailedURL(dto.getFailedURL());
        r.setErrorURL(dto.getErrorURL());
        r.setStatus(TransactionStatus.CREATED);

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
        for(Bank b: bankRepository.findAll()){
            if(b.getPan().equals(requestForIssuerBank.getPan().substring(0, 6))){
                return b.getUrl();
            }
        }
        return "";
    }

}
