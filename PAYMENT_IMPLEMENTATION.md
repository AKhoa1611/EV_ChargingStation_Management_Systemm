# Payment Functionality Implementation

This document provides an overview of the payment functionality implementation for the EV Charging Station Management System.

## Overview

The payment system handles charging session payments with support for multiple payment methods (VNPay, Cash, QR), calculates fees based on power consumption, time-based pricing, subscription discounts, and sends invoice emails after successful payment.

## Architecture

### 1. Entities
- **Session**: Stores charging session details (start/end time, power consumed, cost)
- **Transaction**: Records payment transactions with status tracking
- **Fee**: Stores additional fees (charging fees, penalties)
- **PriceFactor**: Time-based pricing multipliers for stations
- **Subscription**: User subscription information for discounts

### 2. Repositories
- **SessionRepository**: CRUD operations for sessions
- **TransactionRepository**: CRUD operations for transactions with filtering by user/session/status
- **FeeRepository**: CRUD operations for fees with filtering by session/type
- **PriceFactorRepository**: CRUD operations with active price factor lookup
- **SubscriptionRepository**: CRUD operations with active subscription lookup

### 3. Services

#### TransactionService
- Creates and manages transactions
- Converts transactions to DTOs
- Retrieves transactions by user or session

#### PaymentService
- Main payment processing logic
- Calculates total payment amount including:
  - Base charging cost (power × price × factor)
  - Subscription discounts (5% BASIC, 10% PLUS, 15% PREMIUM)
  - Additional fees (penalties, overtime, etc.)
- Processes payments for different methods
- Handles VNPay callbacks
- Sends invoice emails

#### VNPayService
- Generates VNPay payment URLs
- Verifies payment callback signatures
- Translates VNPay response codes to readable messages

#### EmailService
- Sends invoice emails with detailed payment breakdown
- Uses HTML email templates

### 4. Controllers

#### PaymentController
- `POST /api/payment/process`: Process payment for a session
- `GET /api/payment/calculate/{sessionId}`: Calculate payment amount
- `GET /api/payment/transactions/user/{userId}`: Get user transactions
- `GET /api/payment/transactions/session/{sessionId}`: Get session transactions
- `GET /api/payment/transaction/{transactionId}`: Get transaction details

#### VNPayController
- `GET /api/payment/vnpay/callback`: Handle VNPay payment callback
- `POST /api/payment/vnpay/ipn`: Handle VNPay IPN (Instant Payment Notification)

### 5. DTOs
- **PaymentRequestDTO**: Payment initiation request with session ID, payment method, return URL
- **PaymentResponseDTO**: Payment response with transaction ID, amount, status, payment URL
- **TransactionDTO**: Transaction information for API responses
- **InvoiceDTO**: Detailed invoice information for emails

### 6. Utilities

#### PaymentCalculationUtil
- `calculateTotalAmount()`: Calculates total payment including discounts and fees
- `getSubscriptionDiscount()`: Returns discount percentage for subscription type
- `calculateChargingFee()`: Calculates base charging fee

#### VNPayUtil
- `generatePaymentUrl()`: Creates VNPay payment URL with proper signature
- `verifyCallback()`: Verifies VNPay callback signature
- `generateTxnRef()`: Generates unique transaction reference

#### EmailTemplateUtil
- `generateInvoiceEmail()`: Creates HTML email template for invoices

## Payment Flow

### Cash Payment Flow
1. Client sends payment request with CASH method
2. System calculates total amount
3. Transaction is created with PENDING status
4. Transaction is immediately marked as SUCCESS
5. Invoice email is sent to user

### VNPay Payment Flow
1. Client sends payment request with VNPAY method
2. System calculates total amount
3. Transaction is created with PENDING status
4. VNPay payment URL is generated
5. Client is redirected to VNPay
6. User completes payment on VNPay
7. VNPay redirects to callback URL with payment result
8. System verifies callback signature
9. Transaction status is updated based on result
10. Invoice email is sent if payment successful

## Payment Calculation

Formula: `Total = (PowerConsumed × BasePrice × PriceFactor × (1 - Discount)) + AdditionalFees`

Example:
- Power Consumed: 10 kWh
- Base Price: 5,000 VND/kWh
- Price Factor: 1.5 (peak hours)
- Subscription: PLUS (10% discount)
- Additional Fees: 10,000 VND (penalty)

Calculation:
- Base Cost: 10 × 5,000 × 1.5 = 75,000 VND
- After Discount: 75,000 × (1 - 0.10) = 67,500 VND
- Total: 67,500 + 10,000 = 77,500 VND

## Configuration

Add these properties to `application.properties`:

```properties
# VNPay Configuration
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
```

## Entity Updates

The `ChargingPoint` entity was updated to include:
- `powerOutput`: Power output capacity of the charging point
- `pricePerKwh`: Base price per kWh for charging at this point

## Security Considerations

1. VNPay callbacks are verified using HMAC-SHA512 signatures
2. Transaction references should be stored in a mapping table for production use
3. Email sending is currently simulated - integrate with real email service in production

## Future Enhancements

1. Implement real email service integration (JavaMail, SendGrid, etc.)
2. Add txnRef to Transaction mapping table for VNPay callbacks
3. Add payment retry mechanism for failed transactions
4. Implement refund functionality
5. Add payment analytics and reporting
6. Support for more payment methods (credit cards, e-wallets)
7. Add webhook notifications for payment status changes

## Testing

The implementation compiles successfully. Integration tests require:
- SQL Server database connection
- VNPay sandbox credentials
- Email service configuration

Unit tests can be added for:
- Payment calculation logic
- VNPay signature verification
- Email template generation
- Service layer business logic

## API Examples

### Process Cash Payment
```bash
POST /api/payment/process
Content-Type: application/json

{
  "sessionId": 1,
  "paymentMethod": "CASH"
}
```

### Process VNPay Payment
```bash
POST /api/payment/process
Content-Type: application/json

{
  "sessionId": 1,
  "paymentMethod": "VNPAY",
  "returnUrl": "http://example.com/payment-result"
}
```

### Calculate Payment Amount
```bash
GET /api/payment/calculate/1
```

### Get User Transactions
```bash
GET /api/payment/transactions/user/1
```
