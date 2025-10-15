package swp391.code.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp391.code.swp391.entity.Transaction;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    @NotNull(message = "Session ID is required")
    private Long sessionId;
    
    @NotNull(message = "Payment method is required")
    private Transaction.PaymentMethod paymentMethod;
    
    private String returnUrl; // For VNPay callback
}
