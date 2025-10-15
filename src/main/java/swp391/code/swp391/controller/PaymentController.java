package swp391.code.swp391.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.dto.APIResponse;
import swp391.code.swp391.dto.PaymentRequestDTO;
import swp391.code.swp391.dto.PaymentResponseDTO;
import swp391.code.swp391.dto.TransactionDTO;
import swp391.code.swp391.service.PaymentService;
import swp391.code.swp391.service.TransactionService;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final TransactionService transactionService;
    
    @PostMapping("/process")
    @Operation(summary = "Process payment for a charging session")
    public ResponseEntity<APIResponse<PaymentResponseDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest) {
        try {
            PaymentResponseDTO response = paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok(APIResponse.success("Payment processed successfully", response));
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Error encoding payment URL: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Payment processing failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/calculate/{sessionId}")
    @Operation(summary = "Calculate payment amount for a session")
    public ResponseEntity<APIResponse<Double>> calculatePaymentAmount(@PathVariable Long sessionId) {
        try {
            Double amount = paymentService.calculatePaymentAmount(sessionId);
            return ResponseEntity.ok(APIResponse.success("Amount calculated successfully", amount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Failed to calculate amount: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transactions/user/{userId}")
    @Operation(summary = "Get all transactions for a user")
    public ResponseEntity<APIResponse<List<TransactionDTO>>> getUserTransactions(@PathVariable Long userId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId);
            return ResponseEntity.ok(APIResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Failed to retrieve transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transactions/session/{sessionId}")
    @Operation(summary = "Get all transactions for a session")
    public ResponseEntity<APIResponse<List<TransactionDTO>>> getSessionTransactions(@PathVariable Long sessionId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsBySessionId(sessionId);
            return ResponseEntity.ok(APIResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Failed to retrieve transactions: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<APIResponse<TransactionDTO>> getTransaction(@PathVariable Long transactionId) {
        try {
            var transaction = transactionService.getTransactionById(transactionId);
            TransactionDTO dto = transactionService.convertToDTO(transaction);
            return ResponseEntity.ok(APIResponse.success("Transaction retrieved successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("Transaction not found: " + e.getMessage()));
        }
    }
}
