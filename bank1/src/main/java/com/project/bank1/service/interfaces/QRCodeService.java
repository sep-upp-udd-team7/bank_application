package com.project.bank1.service.interfaces;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface QRCodeService {
    void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException;
    byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException;

    String qrCodeGenerator(String text) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException;
    String decodeQRCode(String qr) throws IOException;
}
