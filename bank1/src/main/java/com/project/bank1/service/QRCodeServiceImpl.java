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
import com.project.bank1.service.interfaces.QRCodeService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        //Path path = Paths.get("C:\\SEP-UPP-UDD\\bank_application\\bank1\\src\\main\\resources");

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


    public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000002 , 0xFFFFC041 ) ;

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream,con);
        return pngOutputStream.toByteArray();
    }

    @Override
    public String qrCodeGenerator(String text) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException {

        String filePath = "QRCode.png";
        String charset = "UTF-8";
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        Map<String, String> qrCodeDataMap = Map.of(
                "Primalac", "Kristina Stojic",
                "Cena", "500 din",
                "Racun primaoca", "64879546546",
                "Id transakcije", "5465412"
        );

        String jsonString = new JSONObject(qrCodeDataMap).toString();
        createQRCode(jsonString, filePath, charset, hintMap, 500, 500);

        BufferedImage image = ImageIO.read(new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private void createQRCode(String qrCodeData,
                              String filePath,
                              String charset,
                              Map hintMap,
                              int qrCodeHeight,
                              int qrCodeWidth) throws WriterException,
            IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE,
                qrCodeWidth,
                qrCodeHeight,
                hintMap
        );

        MatrixToImageWriter.writeToPath(
                matrix,
                filePath.substring(filePath.lastIndexOf('.') + 1),
                FileSystems.getDefault().getPath(filePath)
        );
    }


    @Override
    public String decodeQRCode(String qr) throws IOException {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(qr);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println("DEKODIRANO" + result);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }    }

}
