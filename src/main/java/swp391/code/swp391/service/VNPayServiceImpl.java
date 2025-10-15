package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swp391.code.swp391.utils.VNPayUtil;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {
    
    @Value("${vnpay.tmn-code:DEMO_TMN}")
    private String vnpTmnCode;
    
    @Value("${vnpay.hash-secret:DEMO_HASH_SECRET}")
    private String vnpHashSecret;
    
    @Value("${vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpUrl;
    
    @Override
    public String createPaymentUrl(Long transactionId, Long amount, String orderInfo, String returnUrl) 
            throws UnsupportedEncodingException {
        String txnRef = VNPayUtil.generateTxnRef();
        
        return VNPayUtil.generatePaymentUrl(
            amount,
            orderInfo,
            txnRef,
            returnUrl,
            vnpTmnCode,
            vnpHashSecret,
            vnpUrl
        );
    }
    
    @Override
    public boolean verifyPaymentCallback(Map<String, String> params) {
        return VNPayUtil.verifyCallback(params, vnpHashSecret);
    }
    
    @Override
    public String getTransactionStatus(String responseCode) {
        return switch (responseCode) {
            case "00" -> "SUCCESS";
            case "07" -> "TRỪ TIỀN THÀNH CÔNG. GD ĐANG CHỜ XỬ LÝ.";
            case "09" -> "GD KHÔNG THÀNH CÔNG DO: THẺ/TK CỦA BẠN CHƯA ĐĂNG KÝ DỊCH VỤ INTERNETBANKING.";
            case "10" -> "GD KHÔNG THÀNH CÔNG DO: BẠN XÁC THỰC THÔNG TIN THẺ/TK KHÔNG ĐÚNG QUÁ 3 LẦN";
            case "11" -> "GD KHÔNG THÀNH CÔNG DO: ĐÃ HẾT THỜI GIAN CHỜ THANH TOÁN.";
            case "12" -> "GD KHÔNG THÀNH CÔNG DO: THẺ/TK BỊ KHÓA.";
            case "13" -> "GD KHÔNG THÀNH CÔNG DO BẠN NHẬP SAI MẬT KHẨU THANH TOÁN QUAÁ SỐ LẦN.";
            case "24" -> "GD KHÔNG THÀNH CÔNG DO: KHÁCH HÀNG HỦY GD";
            case "51" -> "GD KHÔNG THÀNH CÔNG DO: TÀI KHOẢN KHÔNG ĐỦ SỐ DƯ.";
            case "65" -> "GD KHÔNG THÀNH CÔNG DO: TÀI KHOẢN ĐÃ VƯỢT QUÁ HẠN MỨC GD TRONG NGÀY.";
            case "75" -> "NGÂN HÀNG THANH TOÁN ĐANG BẢO TRÌ.";
            case "79" -> "GD KHÔNG THÀNH CÔNG DO: NHẬP SAI MẬT KHẨU THANH TOÁN QUÁ SỐ LẦN.";
            default -> "GD KHÔNG THÀNH CÔNG";
        };
    }
}
