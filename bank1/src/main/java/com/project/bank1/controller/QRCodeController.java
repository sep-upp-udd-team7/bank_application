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
    public String qrCodeGenerator(@RequestBody GenerateQRCodeDTO dto) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, WriterException {

        String qr = qrService.qrCodeGenerator(dto);
        System.out.println("Generated qr code:" + qr);

        String decoded = new String(qr);
        System.out.println("Decoded:" + decoded);
        return qr;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getQRCodeImage")
    public ResponseEntity<byte[]> qrCodeImageGenerator(@RequestBody GenerateQRCodeDTO dto) throws IOException, WriterException {
        byte[] imageBytes = qrService.qrCodeImageGenerator(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/validateQRCode")
    public ResponseEntity<?> validateQRCode(@RequestBody ValidateQRCodeDTO qr) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, WriterException {

        try {
            Boolean decodedText = qrService.decodeQRCode(qr.getQr());

            if(!decodedText) {
                System.out.println("No QR Code found in the image");
                return new ResponseEntity<>(false, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
