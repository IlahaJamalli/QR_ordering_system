package com.qrrestaurant.backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeGeneratorTest {

    @TempDir
    Path tempDir;

    private QrCodeGenerator qrCodeGenerator;
    private Path qrCodesDir;

    @BeforeEach
    void setUp() {
        qrCodeGenerator = new QrCodeGenerator();
        qrCodesDir = tempDir.resolve("qr-codes");
        qrCodeGenerator.setQrCodesDirectory(qrCodesDir.toString());
    }

    @Test
    void generateQRCodeImage_Success() throws Exception {
        // Arrange
        String content = "https://example.com/table/1";
        String fileName = "table-1.png";
        Path expectedPath = qrCodesDir.resolve(fileName);

        // Act
        qrCodeGenerator.generateQRCodeImage(content, fileName);

        // Assert
        assertTrue(Files.exists(expectedPath));
        assertTrue(Files.size(expectedPath) > 0);
    }

    @Test
    void generateQRCodeImage_InvalidInput() {
        // Arrange
        String content = null;
        String fileName = "test.png";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            qrCodeGenerator.generateQRCodeImage(content, fileName);
        });
    }

    @Test
    void generateQRCodeImage_CreatesDirectory() throws Exception {
        // Arrange
        Files.deleteIfExists(qrCodesDir);
        String content = "https://example.com/table/2";
        String fileName = "table-2.png";
        Path expectedPath = qrCodesDir.resolve(fileName);

        // Act
        qrCodeGenerator.generateQRCodeImage(content, fileName);

        // Assert
        assertTrue(Files.exists(qrCodesDir));
        assertTrue(Files.exists(expectedPath));
        assertTrue(Files.size(expectedPath) > 0);
    }
} 