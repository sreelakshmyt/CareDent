package com.example.caredent.service;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

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
            
            // Note: Flying Saucer expects well-formed XML (XHTML), 
            // so ensure your HTML template is clean.
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
            
            return os.toByteArray();
        } catch (Exception e) {
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        }
    }
}
