package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp391.code.swp391.dto.InvoiceDTO;
import swp391.code.swp391.utils.EmailTemplateUtil;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    @Override
    public void sendInvoiceEmail(String toEmail, InvoiceDTO invoice) {
        String subject = "Hóa đơn thanh toán - EV Charging Station";
        String htmlContent = EmailTemplateUtil.generateInvoiceEmail(invoice);
        sendEmail(toEmail, subject, htmlContent);
    }
    
    @Override
    public void sendEmail(String toEmail, String subject, String content) {
        // Simulate email sending - in production, integrate with actual email service
        System.out.println("=== SENDING EMAIL ===");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Content length: " + content.length() + " characters");
        System.out.println("=====================");
        
        // TODO: Implement real email sending using JavaMail or external service
        // Example with JavaMail:
        // MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        // helper.setTo(toEmail);
        // helper.setSubject(subject);
        // helper.setText(content, true);
        // mailSender.send(message);
    }
}
