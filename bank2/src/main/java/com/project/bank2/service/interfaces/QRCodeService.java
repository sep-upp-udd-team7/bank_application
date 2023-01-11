package com.project.bank2.service.interfaces;

import com.google.zxing.WriterException;
import com.project.bank2.dto.GenerateQRCodeDTO;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface QRCodeService {
    void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException;
    byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException;
    String qrCodeGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException;
    Boolean decodeQRCode(String qr) throws IOException;
}
