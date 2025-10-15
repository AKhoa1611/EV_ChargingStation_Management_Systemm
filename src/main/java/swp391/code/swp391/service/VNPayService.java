package swp391.code.swp391.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface VNPayService {
    String createPaymentUrl(Long transactionId, Long amount, String orderInfo, String returnUrl) throws UnsupportedEncodingException;
    boolean verifyPaymentCallback(Map<String, String> params);
    String getTransactionStatus(String responseCode);
}
