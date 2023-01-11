package com.project.bank1.service.interfaces;

import com.google.zxing.WriterException;
import com.project.bank1.dto.GenerateQRCodeDTO;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface QRCodeService {
    void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException;
//    byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException;
    String qrCodeGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException;
    Boolean decodeQRCode(String qr) throws IOException;
    GenerateQRCodeDTO getQrCodeData(String paymentId);
}
