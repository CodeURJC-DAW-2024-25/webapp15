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

    //generate the PDF from the order data
    public byte[] generatePdfFromOrder(Map<String, Object> orderData) {
        String htmlContent = renderHtmlTemplate(orderData);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            //renderize the PDF with ITextRenderer
            ITextRenderer renderer = new ITextRenderer();

            //stablish the base URL for the images (the base path inside the classpath)
            renderer.getSharedContext().setBaseURL("classpath:/static/");

            //stablish the HTML document for the PDF
            renderer.setDocumentFromString(htmlContent);

            renderer.layout();

            //create the PDF in the OutputStream
            renderer.createPDF(outputStream);

            // return content of the PDF as a byte array
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private String renderHtmlTemplate(Map<String, Object> orderData) {
        try {
            //load the HTML template from the classpath
            ClassPathResource resource = new ClassPathResource("templates/ticket.html");

            //verify if the file exists
            if (!resource.exists()) {
                throw new RuntimeException("HTML template not found");
            }

            InputStream templateStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(templateStream, StandardCharsets.UTF_8);

            //use mustache to process the template
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(reader, "ticket");

            StringWriter writer = new StringWriter();
            mustache.execute(writer, orderData);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("❌ Can not load HTML template", e);
        }
    }
}
