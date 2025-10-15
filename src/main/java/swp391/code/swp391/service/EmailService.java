package swp391.code.swp391.service;

import swp391.code.swp391.dto.InvoiceDTO;

public interface EmailService {
    void sendInvoiceEmail(String toEmail, InvoiceDTO invoice);
    void sendEmail(String toEmail, String subject, String content);
}
