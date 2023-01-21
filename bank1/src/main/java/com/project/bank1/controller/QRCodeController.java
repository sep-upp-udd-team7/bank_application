package com.project.bank1.controller;

import com.google.zxing.WriterException;
import com.project.bank1.dto.GenerateQRCodeDTO;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.dto.ValidateQRCodeDTO;
import com.project.bank1.service.interfaces.QRCodeService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RestController
@RequestMapping(value = "/qr", produces = MediaType.APPLICATION_JSON_VALUE)
public class QRCodeController {

    @Autowired
    QRCodeService qrService;

    @RequestMapping(method = RequestMethod.POST, value = "/getQRCode")
    public ResponseEntity<?> qrCodeGenerator(@RequestBody GenerateQRCodeDTO dto) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, WriterException {

        String qr = qrService.qrCodeGenerator(dto);
        System.out.println("Generated qr code:" + qr);
        if(qr.contains("Qr code failed")){
            return new ResponseEntity<>(qr, HttpStatus.BAD_REQUEST);
        }

        String decoded = new String(qr);
        System.out.println("Decoded:" + decoded);
        return new ResponseEntity<>(qr, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getQRCodeImage")
    public ResponseEntity<?> qrCodeImageGenerator(@RequestBody GenerateQRCodeDTO dto) throws IOException, WriterException {
        byte[] imageBytes = qrService.qrCodeImageGenerator(dto);
        if(imageBytes == null){
            return new ResponseEntity<>("Validation failed, qr code was not generated!!",HttpStatus.BAD_REQUEST);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validateQRCode")
    public ResponseEntity<?> validateQRCode(@RequestBody ValidateQRCodeDTO qr) {

        try {
            String decodedText = qrService.decodeQRCode(qr.getQr());

            if(decodedText.contains("Qr code failed")) {
                System.out.println("No QR Code found in the image");
                return new ResponseEntity<>(decodedText, HttpStatus.BAD_REQUEST);
            } else {
                System.out.println("Qr code is valid!");
                return new ResponseEntity<>("Qr code is valid!", HttpStatus.OK);
            }
        } catch (IOException e) {
            String msg = "Could not decode QR Code, IOException ::"  + e.getMessage();
            System.out.println(msg);
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/getQrCodeData/{paymentId}")
    public ResponseEntity<?> getQrCodeData(@PathVariable String paymentId){
        GenerateQRCodeDTO qr = qrService.getQrCodeData(paymentId);
        if(qr == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(qr, HttpStatus.OK);
    }

}
