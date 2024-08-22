package com.example.authenticationauthorization.otherService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IConvertPdfService {
     void convertToPdf(String inputFilePath, String outputDirectory);
}
