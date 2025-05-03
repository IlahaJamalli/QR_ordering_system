package com.qrrestaurant.backend.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {

    public static String generateQRCodeImage(String text, String fileName) throws WriterException, IOException {
        int width = 350;
        int height = 350;
        String filePath = "qrcodes/" + fileName + ".png";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);  // Less white border

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        Path path = FileSystems.getDefault().getPath(filePath);
        File dir = new File("qrcodes");
        if (!dir.exists()) dir.mkdirs();  // Create folder if not exist

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
    }
}
