package com.qrrestaurant.backend.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class QrCodeGenerator {

    @Value("${qr.codes.directory:qr-codes}")
    private String qrCodesDirectory;

    public void generateQRCodeImage(String text, String fileName) throws IOException, WriterException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        // Create QR codes directory if it doesn't exist
        Path qrCodesPath = Paths.get(qrCodesDirectory);
        if (!Files.exists(qrCodesPath)) {
            Files.createDirectories(qrCodesPath);
        }

        // Generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        // Write QR code to file
        Path filePath = qrCodesPath.resolve(fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
    }

    // For testing purposes
    void setQrCodesDirectory(String directory) {
        this.qrCodesDirectory = directory;
    }
}
