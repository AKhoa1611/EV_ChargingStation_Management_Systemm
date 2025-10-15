package swp391.code.swp391.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.dto.APIResponse;
import swp391.code.swp391.dto.PaymentResponseDTO;
import swp391.code.swp391.service.PaymentService;
import swp391.code.swp391.service.VNPayService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay", description = "VNPay payment callback APIs")
public class VNPayController {
    
    private final PaymentService paymentService;
    private final VNPayService vnPayService;
    
    @GetMapping("/callback")
    @Operation(summary = "VNPay payment callback handler")
    public ResponseEntity<APIResponse<PaymentResponseDTO>> handleVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            // Verify callback signature
            boolean isValid = vnPayService.verifyPaymentCallback(new HashMap<>(params));
            
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.error("Invalid payment callback signature"));
            }
            
            // Extract payment information
            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");
            
            // Extract transaction ID from txnRef (assuming format: TXN{timestamp})
            // In production, you should store txnRef mapping to transactionId
            Long transactionId = extractTransactionId(txnRef);
            
            // Process payment callback
            PaymentResponseDTO response = paymentService.processVNPayCallback(transactionId, responseCode);
            
            return ResponseEntity.ok(APIResponse.success("Payment callback processed", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Error processing payment callback: " + e.getMessage()));
        }
    }
    
    @PostMapping("/ipn")
    @Operation(summary = "VNPay IPN (Instant Payment Notification) handler")
    public ResponseEntity<Map<String, String>> handleVNPayIPN(@RequestParam Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Verify IPN signature
            boolean isValid = vnPayService.verifyPaymentCallback(new HashMap<>(params));
            
            if (!isValid) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                return ResponseEntity.ok(response);
            }
            
            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");
            Long transactionId = extractTransactionId(txnRef);
            
            // Process payment
            paymentService.processVNPayCallback(transactionId, responseCode);
            
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Extract transaction ID from VNPay transaction reference
     * This is a simplified implementation - in production, maintain a mapping table
     */
    private Long extractTransactionId(String txnRef) {
        // For demo purposes, you might need to maintain a mapping
        // between txnRef and transactionId in a separate table
        // Here we'll just throw an exception to remind implementation
        throw new RuntimeException("Transaction ID extraction not implemented. " +
                                  "Please implement proper txnRef to transactionId mapping.");
    }
}
