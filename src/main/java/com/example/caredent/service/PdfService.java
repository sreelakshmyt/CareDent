package com.example.caredent.service;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdfFromHtml(String templateName, Context context) throws DocumentException {
        // 1. Process Thymeleaf template to get final HTML string
        String htmlContent = templateEngine.process(templateName, context);

        // 2. Use Flying Saucer (ITextRenderer) to convert HTML to PDF bytes
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            
            // NOTE: If you are using external images/fonts, you MUST set the document base.
            // Example for resources in src/main/resources/static:
            // String basePath = "file:///" + System.getProperty("user.dir") + "/src/main/resources/static/";
            // renderer.setDocumentBase(basePath); 

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
            
            return os.toByteArray();
        
        } catch (IOException e) {
             // Handle IO issues 
            throw new DocumentException("IO Error during PDF generation: " + e.getMessage());
        } catch (Exception e) {
             // Catch all other exceptions (like SAXParseException from bad HTML)
             // Use the constructor that accepts only a String message
             throw new DocumentException("Error during HTML rendering/layout. Cause: " + e.getMessage());
        }
    }
}