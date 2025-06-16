package com.example.furniturestore.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.repository.OrderRepository;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final OrderRepository orderRepository;

    public ReportController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping(value = "/sales.csv", produces = "text/csv")
    public ResponseEntity<String> salesCsv() {
        List<Order> orders = orderRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,customer,date,total\n");
        for (Order o : orders) {
            sb.append(o.getId()).append(',')
              .append('"').append(o.getCustomerName()).append('"').append(',')
              .append(o.getOrderDate().format(DateTimeFormatter.ISO_DATE)).append(',')
              .append(o.getTotalPrice()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales.csv")
                .body(sb.toString());
    }

    @GetMapping(value = "/sales.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> salesPdf() throws IOException {
        List<Order> orders = orderRepository.findAll();
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.newLineAtOffset(50, 700);
                content.showText("Sales Report");
                content.endText();
                int y = 660;
                content.setFont(PDType1Font.HELVETICA, 12);
                for (Order o : orders) {
                    if (y < 50) {
                        break; // simple single-page report
                    }
                    content.beginText();
                    content.newLineAtOffset(50, y);
                    String line = o.getId() + " - " + o.getCustomerName() + " - " +
                            o.getOrderDate().format(DateTimeFormatter.ISO_DATE) + " - $" + o.getTotalPrice();
                    content.showText(line);
                    content.endText();
                    y -= 15;
                }
            }
            doc.save(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales.pdf")
                    .body(out.toByteArray());
        }
    }
}
