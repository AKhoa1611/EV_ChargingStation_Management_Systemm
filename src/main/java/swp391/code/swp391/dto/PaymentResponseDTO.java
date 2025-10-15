package swp391.code.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp391.code.swp391.entity.Transaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long transactionId;
    private Long sessionId;
    private Double totalAmount;
    private Transaction.PaymentMethod paymentMethod;
    private Transaction.Status status;
    private String paymentUrl; // For VNPay redirect URL
    private String message;
}
