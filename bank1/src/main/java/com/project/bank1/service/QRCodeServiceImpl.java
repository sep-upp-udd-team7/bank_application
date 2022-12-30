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
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    @Override
    public String qrCodeGenerator(String text) throws IOException, WriterException, InvalidKeySpecException, NoSuchAlgorithmException {

        String filePath = "QRCode.png";
        String charset = "UTF-8";
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        Map<String, String> qrCodeDataMap = Map.of(
                "Prodavac", "Kristina Stojic",
                "Cijena", "500 din"
                // see next section for ´generateVerificationKey´ method
        );

        String jsonString = new JSONObject(qrCodeDataMap).toString();
        createQRCode(jsonString, filePath, charset, hintMap, 500, 500);

        BufferedImage image = ImageIO.read(new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageData = baos.toByteArray();
        String slika = Base64.getEncoder().encodeToString(baos.toByteArray());
        return slika;
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
    public String decodeQRCode(File qrCodeimage) throws IOException {
        byte[] decodedBytes = Base64.getMimeDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAfQAAAH0AQAAAADjreInAAACsElEQVR4Xu2bMXLcMAxF6XGR0kfQUXQ07dF0FB3B5RYZMwL4SUqwnQnjku81BkA8boUhV1qn/CPeU6yMgR8rY+DHyhj4sTIGfqyMgR8rY+DHyhj4sTIGfqyMgR8rY+DHyhj4sTIGvoJn6pzJ6sXfaclnXpKcj97yqhK+AvzOnH7vKsm79S1951dbEQ/8Ar4C/PWjdZ3KS+tSkrXzyRs+fgFfQfSt6zy/Tn7l/FEUfHz8f/Bz3tV1WM3ZMj4+/ve+ot7l82fJy9lXd3bwFeArwO/YZtcjqycNfHz8ix9ph1llix0OfqwI/Ln8px45WKvl5m8WnN+fdp1fJfHdPs0fPn6e2l/9lErlkfdqS6d/WHNd8ZOtfAw+Pv7dV3TYI7vdujd/fufUL1PZhhEfX834Cm4LmrJCHTnbprY18FuIn/HrlDm3zTyxycS/gN/C6X07pR6p3P9ufL7/NfBbuOPP7Wvh8J8s2FK6jZwug2Uzn0xvUoCPX/7O6fdHDs7uX5lsMxs5Y3PfN+tt+PgF/IK/ctUwPvz9kRQfxrrSwFeAP73fFk4l+/sjG7msJ3u+mSXfnV/4CvEn9JWl2qUk+//fmZL9/Er2MX0Y8fEvzO2ra9Ew1lueksbX51fGx5/Y96565esstrb7Si2pDR8f/zp/Hd/s3e5/S7sMJleuJ5uB360O/nT+01pEXSp+KpulejO0Y+6Bj49/81dFvasmBc1f+vr8wl8V4U/q65QqikYu9/mrmN/Ax8ePfrb3r/78e9H97638MuhMfMV3xsfH/4vvyclqDc1vk4mPj999RYfOr3rLe9oW/pWpboaPr2Z8Bd4lit+7dvmlzVY2Wfj4hdn9/wQ/VsbAj5Ux8GNlDPxYGQM/VsbAj5Ux8GNlDPxYGQM/VsbAj5Ux8GNlDPxYGePH/h+X9flyPdzEPQAAAABJRU5ErkJggg==");


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
