package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.code.swp391.dto.InvoiceDTO;
import swp391.code.swp391.dto.PaymentRequestDTO;
import swp391.code.swp391.dto.PaymentResponseDTO;
import swp391.code.swp391.entity.*;
import swp391.code.swp391.repository.*;
import swp391.code.swp391.utils.PaymentCalculationUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final TransactionService transactionService;
    private final SessionRepository sessionRepository;
    private final FeeRepository feeRepository;
    private final PriceFactorRepository priceFactorRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VNPayService vnPayService;
    private final EmailService emailService;
    
    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) throws UnsupportedEncodingException {
        // Get session
        Session session = sessionRepository.findById(paymentRequest.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + paymentRequest.getSessionId()));
        
        // Calculate total amount
        Double totalAmount = calculatePaymentAmount(paymentRequest.getSessionId());
        
        // Create transaction
        Transaction transaction = transactionService.createTransaction(
            paymentRequest.getSessionId(),
            session.getOrder().getUser().getUserId(),
            totalAmount,
            paymentRequest.getPaymentMethod()
        );
        
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setTransactionId(transaction.getTransactionId());
        response.setSessionId(paymentRequest.getSessionId());
        response.setTotalAmount(totalAmount);
        response.setPaymentMethod(paymentRequest.getPaymentMethod());
        
        // Process based on payment method
        if (paymentRequest.getPaymentMethod() == Transaction.PaymentMethod.VNPAY) {
            // Generate VNPay payment URL
            String orderInfo = "Thanh toan hoa don sac xe - Session #" + session.getSessionId();
            String returnUrl = paymentRequest.getReturnUrl() != null ? 
                              paymentRequest.getReturnUrl() : 
                              "http://localhost:8080/api/payment/vnpay/callback";
            
            String paymentUrl = vnPayService.createPaymentUrl(
                transaction.getTransactionId(),
                totalAmount.longValue(),
                orderInfo,
                returnUrl
            );
            
            response.setPaymentUrl(paymentUrl);
            response.setStatus(Transaction.Status.PENDING);
            response.setMessage("Vui lòng thanh toán qua VNPay");
        } else if (paymentRequest.getPaymentMethod() == Transaction.PaymentMethod.CASH) {
            // For cash payment, mark as success immediately
            transactionService.updateTransactionStatus(transaction.getTransactionId(), Transaction.Status.SUCCESS);
            response.setStatus(Transaction.Status.SUCCESS);
            response.setMessage("Thanh toán bằng tiền mặt thành công");
            
            // Send invoice email
            sendInvoiceEmail(transaction);
        } else {
            // QR payment or other methods
            response.setStatus(Transaction.Status.PENDING);
            response.setMessage("Vui lòng thanh toán qua " + paymentRequest.getPaymentMethod());
        }
        
        return response;
    }
    
    @Override
    public PaymentResponseDTO processVNPayCallback(Long transactionId, String responseCode) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setTransactionId(transactionId);
        response.setSessionId(transaction.getSession().getSessionId());
        response.setTotalAmount(transaction.getAmount());
        response.setPaymentMethod(transaction.getPaymentMethod());
        
        if ("00".equals(responseCode)) {
            // Payment successful
            transactionService.updateTransactionStatus(transactionId, Transaction.Status.SUCCESS);
            response.setStatus(Transaction.Status.SUCCESS);
            response.setMessage("Thanh toán thành công");
            
            // Send invoice email
            sendInvoiceEmail(transaction);
        } else {
            // Payment failed
            transactionService.updateTransactionStatus(transactionId, Transaction.Status.FAILED);
            response.setStatus(Transaction.Status.FAILED);
            response.setMessage(vnPayService.getTransactionStatus(responseCode));
        }
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculatePaymentAmount(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        
        // Get base price from charging point
        Double basePrice = session.getOrder().getChargingPoint().getPricePerKwh();
        
        // Get price factor (time-based pricing)
        Double priceFactor = 1.0; // Default factor
        Long stationId = session.getOrder().getStation().getStationId();
        LocalDateTime sessionTime = convertToLocalDateTime(session.getStartTime());
        
        var priceFactorOpt = priceFactorRepository.findActiveFactorForStation(stationId, sessionTime);
        if (priceFactorOpt.isPresent()) {
            priceFactor = priceFactorOpt.get().getFactor();
        }
        
        // Get user's active subscription
        Long userId = session.getOrder().getUser().getUserId();
        Subscription.Type subscriptionType = null;
        var subscriptionOpt = subscriptionRepository.findActiveSubscriptionForUser(userId, sessionTime);
        if (subscriptionOpt.isPresent()) {
            subscriptionType = subscriptionOpt.get().getType();
        }
        
        // Get additional fees
        List<Fee> fees = feeRepository.findBySession_SessionId(sessionId);
        Double totalFees = fees.stream()
                .mapToDouble(Fee::getAmount)
                .sum();
        
        // Calculate total amount
        return PaymentCalculationUtil.calculateTotalAmount(
            session.getPowerConsumed(),
            basePrice,
            priceFactor,
            subscriptionType,
            totalFees
        );
    }
    
    private void sendInvoiceEmail(Transaction transaction) {
        try {
            InvoiceDTO invoice = buildInvoice(transaction);
            emailService.sendInvoiceEmail(transaction.getUser().getEmail(), invoice);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send invoice email: " + e.getMessage());
        }
    }
    
    private InvoiceDTO buildInvoice(Transaction transaction) {
        Session session = transaction.getSession();
        User user = transaction.getUser();
        ChargingStation station = session.getOrder().getStation();
        ChargingPoint chargingPoint = session.getOrder().getChargingPoint();
        
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setTransactionId(transaction.getTransactionId());
        invoice.setSessionId(session.getSessionId());
        invoice.setUserName(user.getFullName());
        invoice.setUserEmail(user.getEmail());
        invoice.setStationName(station.getStationName());
        invoice.setStationAddress(station.getAddress());
        invoice.setStartTime(session.getStartTime());
        invoice.setEndTime(session.getEndTime());
        invoice.setPowerConsumed(session.getPowerConsumed());
        invoice.setBasePrice(chargingPoint.getPricePerKwh());
        
        // Get price factor
        Double priceFactor = 1.0;
        LocalDateTime sessionTime = convertToLocalDateTime(session.getStartTime());
        var priceFactorOpt = priceFactorRepository.findActiveFactorForStation(
            station.getStationId(), sessionTime);
        if (priceFactorOpt.isPresent()) {
            priceFactor = priceFactorOpt.get().getFactor();
        }
        invoice.setPriceFactor(priceFactor);
        
        // Get subscription discount
        Double discount = 0.0;
        var subscriptionOpt = subscriptionRepository.findActiveSubscriptionForUser(
            user.getUserId(), sessionTime);
        if (subscriptionOpt.isPresent()) {
            discount = PaymentCalculationUtil.getSubscriptionDiscount(subscriptionOpt.get().getType());
        }
        invoice.setSubscriptionDiscount(discount);
        
        // Get fees
        List<Fee> fees = feeRepository.findBySession_SessionId(session.getSessionId());
        List<InvoiceDTO.FeeDetail> feeDetails = fees.stream()
            .map(fee -> new InvoiceDTO.FeeDetail(
                fee.getType().toString(),
                fee.getAmount(),
                "Additional " + fee.getType() + " fee"
            ))
            .collect(Collectors.toList());
        invoice.setFees(feeDetails);
        
        // Calculate subtotal and total
        Double chargingCost = PaymentCalculationUtil.calculateChargingFee(
            session.getPowerConsumed(), chargingPoint.getPricePerKwh(), priceFactor);
        Double discountedCost = chargingCost * (1 - discount);
        invoice.setSubtotal(discountedCost);
        invoice.setTotalAmount(transaction.getAmount());
        
        invoice.setPaymentMethod(transaction.getPaymentMethod().toString());
        invoice.setPaymentDate(new Date());
        
        return invoice;
    }
    
    private LocalDateTime convertToLocalDateTime(Date date) {
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime();
    }
}
