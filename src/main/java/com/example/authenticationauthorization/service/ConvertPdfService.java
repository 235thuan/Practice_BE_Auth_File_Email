package com.example.authenticationauthorization.service;


import com.example.authenticationauthorization.otherService.IConvertPdfService;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ConvertPdfService implements IConvertPdfService {
    @Override
    public void convertToPdf(String inputFilePath, String outputDirectory) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("soffice", "--headless", "--convert-to", "pdf", inputFilePath, "--outdir", outputDirectory);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Conversion successful!");
            } else {
                System.out.println("Conversion failed!");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
