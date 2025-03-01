package com.stepx.stepx.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

@Service
public class PdfService {

    public byte[] generatePdfFromOrder(Map<String, Object> orderData) {
        String htmlContent = renderHtmlTemplate(orderData);
    
        // 🔹 Imprimir el HTML generado para depuración
        System.out.println("📄 HTML generado:\n" + htmlContent);
    
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String renderHtmlTemplate(Map<String, Object> orderData) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/ticket.html");
    
            // 🔹 Verificar si el archivo existe
            if (!resource.exists()) {
                System.out.println("❌ Error: La plantilla no fue encontrada en 'src/main/resources/templates/'");
                throw new RuntimeException("Plantilla HTML no encontrada");
            }
    
            InputStream templateStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(templateStream, StandardCharsets.UTF_8);
    
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(reader, "ticket");
    
            StringWriter writer = new StringWriter();
            mustache.execute(writer, orderData);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("❌ No se pudo cargar la plantilla HTML", e);
        }
    }
}


