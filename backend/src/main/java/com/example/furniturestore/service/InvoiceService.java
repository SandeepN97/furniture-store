package com.example.furniturestore.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.OrderItem;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.newLineAtOffset(50, 700);
                content.showText("Invoice #" + order.getId());
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 680);
                content.showText("Customer: " + order.getCustomerName());
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 665);
                content.showText("Date: " + order.getOrderDate().format(DateTimeFormatter.ISO_DATE));
                content.endText();

                int y = 640;
                for (OrderItem item : order.getItems()) {
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 12);
                    content.newLineAtOffset(50, y);
                    content.showText(item.getProduct().getName() + " x" + item.getQuantity() +
                            " - $" + item.getPrice());
                    content.endText();
                    y -= 15;
                }

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, y - 10);
                content.showText("Total: $" + order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                content.endText();
            }
            doc.save(out);
            return out.toByteArray();
        }
    }
}
