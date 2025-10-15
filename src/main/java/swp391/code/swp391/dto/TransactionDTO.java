package swp391.code.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp391.code.swp391.entity.Transaction;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private Long sessionId;
    private Long userId;
    private String userEmail;
    private String userName;
    private Double amount;
    private Transaction.PaymentMethod paymentMethod;
    private Transaction.Status status;
    private LocalDateTime createdAt;
}
