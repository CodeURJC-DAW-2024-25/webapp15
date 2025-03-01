package com.stepx.stepx.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generatePdfFromOrder(Map<String, Object> orderData) {
        String htmlContent = renderHtmlTemplate(orderData);

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
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new InputStreamReader(
                getClass().getResourceAsStream("ticket.html"), StandardCharsets.UTF_8), "ticket");

        StringWriter writer = new StringWriter();
        mustache.execute(writer, orderData);
        return writer.toString();
    }
}

