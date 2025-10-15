package swp391.code.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long transactionId;
    private Long sessionId;
    private String userName;
    private String userEmail;
    private String stationName;
    private String stationAddress;
    private Date startTime;
    private Date endTime;
    private Double powerConsumed;
    private Double basePrice;
    private Double priceFactor;
    private Double subscriptionDiscount;
    private List<FeeDetail> fees;
    private Double subtotal;
    private Double totalAmount;
    private String paymentMethod;
    private Date paymentDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeeDetail {
        private String feeType;
        private Double amount;
        private String description;
    }
}
