package com.stepx.stepx.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class PdfService {

    // Genera el PDF a partir de los datos de la orden
    public byte[] generatePdfFromOrder(Map<String, Object> orderData) {
        String htmlContent = renderHtmlTemplate(orderData);

        // Imprimir el HTML generado para depuración
        System.out.println("📄 HTML generado:\n" + htmlContent);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Renderizar el PDF con ITextRenderer
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.getSharedContext().setBaseURL("file:///" + new File("src/main/resources").getAbsolutePath());
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Renderiza la plantilla HTML con los datos proporcionados
    private String renderHtmlTemplate(Map<String, Object> orderData) {
        try {
            // Cargar la plantilla HTML desde el classpath
            ClassPathResource resource = new ClassPathResource("templates/ticket.html");

            // Verificar si el archivo existe
            if (!resource.exists()) {
                System.out.println("❌ Error: La plantilla no fue encontrada en 'src/main/resources/templates/'");
                throw new RuntimeException("Plantilla HTML no encontrada");
            }

            InputStream templateStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(templateStream, StandardCharsets.UTF_8);

            // Usar Mustache para procesar la plantilla
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
