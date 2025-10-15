package swp391.code.swp391.service;

import swp391.code.swp391.dto.PaymentRequestDTO;
import swp391.code.swp391.dto.PaymentResponseDTO;

import java.io.UnsupportedEncodingException;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) throws UnsupportedEncodingException;
    PaymentResponseDTO processVNPayCallback(Long transactionId, String responseCode);
    Double calculatePaymentAmount(Long sessionId);
}
