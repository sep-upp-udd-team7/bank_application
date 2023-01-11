package com.project.bank1.service.interfaces;

import com.google.zxing.WriterException;
import com.project.bank1.dto.GenerateQRCodeDTO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface QRCodeService {
    String qrCodeGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException;
    Boolean decodeQRCode(String qr) throws IOException;
    GenerateQRCodeDTO getQrCodeData(String paymentId);
    byte[] qrCodeImageGenerator(GenerateQRCodeDTO dto) throws IOException, WriterException;
}
