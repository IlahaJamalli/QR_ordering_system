package com.qrrestaurant.backend.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class QrCodeGenerator {
    private String qrCodesDirectory = "qrcodes";

    public void setQrCodesDirectory(String directory) {
        this.qrCodesDirectory = directory;
    }

    public String generateQRCodeImage(String text, String fileName) throws WriterException, IOException {
        if (text == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }

        int width = 350;
        int height = 350;
        String filePath = qrCodesDirectory + "/" + fileName;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);  // Less white border

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        Path path = FileSystems.getDefault().getPath(filePath);
        File dir = new File(qrCodesDirectory);
        if (!dir.exists()) dir.mkdirs();  // Create folder if not exist

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
    }
}
