package com.project.bank1.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.project.bank1.dto.GenerateQRCodeDTO;
import com.project.bank1.model.Transaction;
import com.project.bank1.service.interfaces.QRCodeService;
import com.project.bank1.service.interfaces.TransactionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Autowired
    TransactionService transactionService;




    @Override
    public String qrCodeGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException {
        String filePath = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), "bank1", "src", "main", "resources", "qr.png").toString();

        if(!createQRCode(dto)){
            return null;
        }

        BufferedImage image = ImageIO.read(new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    @Override
    public byte[] qrCodeImageGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException {
        if(!createQRCode(dto)){
            return null;
        }
        Path filePath = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), "bank1", "src", "main", "resources", "qr.png");
        return Files.readAllBytes(filePath);
    }

    private Boolean createQRCode(GenerateQRCodeDTO dto) throws WriterException, IOException {
        String filePath = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), "bank1", "src", "main", "resources", "qr.png").toString();
        String charset = "UTF-8";
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        if(dto.getAmount()==null || dto.getReceiver()==null || dto.getAccountNumber() ==null || dto.getIdTransaction()==null){
            return false;
        }

        Map<String, String> qrCodeDataMap = Map.of(
                "Receiver", dto.getReceiver(),
                "Amount", dto.getAmount().toString(),
                "Bank account", dto.getAccountNumber(),
                "Id transaction", dto.getIdTransaction()
        );
        JSONObject json = new JSONObject(qrCodeDataMap);
        String jsonString = new JSONObject(qrCodeDataMap).toString();

        if(json.length() != 4){
            return false;
        }
        String amount = json.getString("Amount");
        String receiver = json.getString("Receiver");
        String bankAccount = json.getString("Bank account");
        String idTransaction = json.getString("Id transaction");

        if(amount == null || receiver == null || bankAccount == null || idTransaction == null){
            return false;
        }

        //Amount
        try {
            double a = Double.parseDouble(amount);
            if(a < 0){
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }

        //Receiver bank account
        try {
            double ba = Double.parseDouble(bankAccount);
        } catch (NumberFormatException nfe) {
            return false;
        }


        if(!idTransaction.matches("[a-zA-Z0-9]+")
        || !receiver.matches("[a-zA-Z0-9 ]+")){
            return false;
        }

        BitMatrix matrix = new MultiFormatWriter().encode(new String(jsonString.getBytes(charset), charset), BarcodeFormat.QR_CODE, 250, 250, hintMap);

        MatrixToImageWriter.writeToPath(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), FileSystems.getDefault().getPath(filePath));

        return true;
    }


    @Override
    public Boolean decodeQRCode(String qr) throws IOException {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(qr);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println("Decoded" + result);

            JSONObject json = new JSONObject(result.getText());

            if(json.length() != 4){
                return false;
            }

            //Amount
            try {
                double amount = Double.parseDouble(json.getString("Amount"));
                if(amount < 0){
                    return false;
                }
            } catch (NumberFormatException nfe) {
                return false;
            }

            //Receiver bank account
            try {
                double bankAccount = Double.parseDouble(json.getString("Bank account"));
            } catch (NumberFormatException nfe) {
                return false;
            }
            //kod primaoca samo da budu slova i brojevi

            return true;
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return false;
        }
    }


    @Override
    public GenerateQRCodeDTO getQrCodeData(String paymentId) {
        Transaction t = transactionService.findByPaymentId(paymentId.toString());
        GenerateQRCodeDTO qr = new GenerateQRCodeDTO();
        qr.setAmount(t.getAmount());
        qr.setReceiver(t.getBankAccount().getClient().getName());
        qr.setAccountNumber(t.getBankAccount().getBankAccountNumber());
        qr.setIdTransaction(t.getId());
        return qr;
    }

}
