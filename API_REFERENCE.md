# Payment API Quick Reference

## Base URL
`http://localhost:8080/api/payment`

## Payment Endpoints

### 1. Process Payment
**Endpoint:** `POST /api/payment/process`  
**Description:** Initiate payment for a charging session  
**Request Body:**
```json
{
  "sessionId": 1,
  "paymentMethod": "CASH|VNPAY|QR",
  "returnUrl": "http://example.com/callback" // Optional, for VNPay only
}
```
**Response:**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "transactionId": 1,
    "sessionId": 1,
    "totalAmount": 77500.0,
    "paymentMethod": "CASH",
    "status": "SUCCESS",
    "paymentUrl": null, // VNPay URL if method is VNPAY
    "message": "Thanh toán bằng tiền mặt thành công"
  },
  "timestamp": "2025-10-15T07:14:48.446Z"
}
```

### 2. Calculate Payment Amount
**Endpoint:** `GET /api/payment/calculate/{sessionId}`  
**Description:** Calculate total payment amount for a session  
**Response:**
```json
{
  "success": true,
  "message": "Amount calculated successfully",
  "data": 77500.0,
  "timestamp": "2025-10-15T07:14:48.446Z"
}
```

### 3. Get User Transactions
**Endpoint:** `GET /api/payment/transactions/user/{userId}`  
**Description:** Retrieve all transactions for a specific user  
**Response:**
```json
{
  "success": true,
  "message": "Transactions retrieved successfully",
  "data": [
    {
      "transactionId": 1,
      "sessionId": 1,
      "userId": 1,
      "userEmail": "user@example.com",
      "userName": "John Doe",
      "amount": 77500.0,
      "paymentMethod": "CASH",
      "status": "SUCCESS",
      "createdAt": "2025-10-15T07:14:48"
    }
  ],
  "timestamp": "2025-10-15T07:14:48.446Z"
}
```

### 4. Get Session Transactions
**Endpoint:** `GET /api/payment/transactions/session/{sessionId}`  
**Description:** Retrieve all transactions for a specific session  
**Response:** Same format as Get User Transactions

### 5. Get Transaction Details
**Endpoint:** `GET /api/payment/transaction/{transactionId}`  
**Description:** Retrieve details of a specific transaction  
**Response:**
```json
{
  "success": true,
  "message": "Transaction retrieved successfully",
  "data": {
    "transactionId": 1,
    "sessionId": 1,
    "userId": 1,
    "userEmail": "user@example.com",
    "userName": "John Doe",
    "amount": 77500.0,
    "paymentMethod": "CASH",
    "status": "SUCCESS",
    "createdAt": "2025-10-15T07:14:48"
  },
  "timestamp": "2025-10-15T07:14:48.446Z"
}
```

## VNPay Endpoints

### 6. VNPay Callback
**Endpoint:** `GET /api/payment/vnpay/callback`  
**Description:** Handle VNPay payment callback (called by VNPay)  
**Query Parameters:** Various VNPay parameters including `vnp_ResponseCode`, `vnp_TxnRef`, `vnp_SecureHash`, etc.  
**Response:**
```json
{
  "success": true,
  "message": "Payment callback processed",
  "data": {
    "transactionId": 1,
    "sessionId": 1,
    "totalAmount": 77500.0,
    "paymentMethod": "VNPAY",
    "status": "SUCCESS",
    "message": "Thanh toán thành công"
  },
  "timestamp": "2025-10-15T07:14:48.446Z"
}
```

### 7. VNPay IPN
**Endpoint:** `POST /api/payment/vnpay/ipn`  
**Description:** Handle VNPay IPN (Instant Payment Notification)  
**Response:**
```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

## Payment Methods
- `CASH`: Cash payment (immediately marked as successful)
- `VNPAY`: VNPay online payment (requires redirect to VNPay)
- `QR`: QR code payment (status pending until confirmed)

## Transaction Status
- `PENDING`: Payment initiated but not completed
- `SUCCESS`: Payment completed successfully
- `FAILED`: Payment failed

## VNPay Response Codes
- `00`: Payment successful
- `07`: Transaction successful, awaiting processing
- `09`: Card not registered for internet banking
- `10`: Authentication failed (exceeded retry limit)
- `11`: Payment timeout
- `12`: Card/account locked
- `13`: Wrong OTP (exceeded retry limit)
- `24`: Customer canceled transaction
- `51`: Insufficient balance
- `65`: Account exceeded daily transaction limit
- `75`: Bank under maintenance
- `79`: Wrong payment password (exceeded retry limit)

## Payment Calculation Formula

```
Total = (PowerConsumed × BasePrice × PriceFactor × (1 - Discount)) + AdditionalFees

Where:
- PowerConsumed: kWh consumed during charging
- BasePrice: Price per kWh from charging point
- PriceFactor: Time-based multiplier (e.g., 1.5 for peak hours)
- Discount: Subscription discount (0.05/0.10/0.15 for BASIC/PLUS/PREMIUM)
- AdditionalFees: Sum of all penalty fees
```

## Subscription Discounts
- **BASIC**: 5% discount
- **PLUS**: 10% discount
- **PREMIUM**: 15% discount
- **No subscription**: 0% discount

## Testing with cURL

### Process Cash Payment
```bash
curl -X POST http://localhost:8080/api/payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "paymentMethod": "CASH"
  }'
```

### Process VNPay Payment
```bash
curl -X POST http://localhost:8080/api/payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "paymentMethod": "VNPAY",
    "returnUrl": "http://localhost:3000/payment-result"
  }'
```

### Calculate Payment Amount
```bash
curl -X GET http://localhost:8080/api/payment/calculate/1
```

### Get User Transactions
```bash
curl -X GET http://localhost:8080/api/payment/transactions/user/1
```

### Get Session Transactions
```bash
curl -X GET http://localhost:8080/api/payment/transactions/session/1
```

### Get Transaction Details
```bash
curl -X GET http://localhost:8080/api/payment/transaction/1
```

## Swagger UI
Access interactive API documentation at:
`http://localhost:8080/swagger-ui.html`

## Notes
1. VNPay sandbox credentials are configured in `application.properties`
2. Email sending is currently simulated (prints to console)
3. For production, update VNPay credentials and implement real email service
4. Transaction reference mapping for VNPay callbacks needs implementation in production
