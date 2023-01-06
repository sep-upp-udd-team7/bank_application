package com.project.bank1.controller;

import com.google.zxing.WriterException;
import com.project.bank1.dto.GenerateQRCodeDTO;
import com.project.bank1.dto.LoginDto;
import com.project.bank1.dto.ValidateQRCodeDTO;
import com.project.bank1.service.interfaces.QRCodeService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RestController
@RequestMapping(value = "/qr", produces = MediaType.APPLICATION_JSON_VALUE)
public class QRCodeController {

    @Autowired
    QRCodeService qrService;

    private static final String QR_CODE_IMAGE_PATH = "C:\\SEP-UPP-UDD\\bank_application\\bank1\\src\\main\\resources\\QRCode.png";

    @GetMapping("/getQR")
    public String getQRCode(Model model){
        String medium="https://rahul26021999.medium.com/";
        String github="Cao, POKUSAVAM DA DEKODIRAM OVO";

        byte[] image = new byte[0];
        try {

            // Generate and Return Qr Code in Byte Array
            image = qrService.getQRCodeImage(github,250,250);

            // Generate and Save Qr Code Image in static/image folder
            qrService.generateQRCodeImage(github,250,250,QR_CODE_IMAGE_PATH);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        // Convert Byte Array into Base64 Encode String
        String qrcode = Base64.getEncoder().encodeToString(image);

        model.addAttribute("medium",medium);
        model.addAttribute("github",github);
        model.addAttribute("qrcode",qrcode);

        System.out.println("MODEL" + model);

        return "qrcode";
    }


    @GetMapping("/getQRCode")
    public String qrCodeGenerator(@RequestBody GenerateQRCodeDTO dto) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, WriterException {

        String qr = qrService.qrCodeGenerator(dto);
        System.out.println("novi qr:" + qr);

        String decodirano = new String(qr);
        System.out.println("DEKODIRANO:" + decodirano);
        return "qrcode";
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
