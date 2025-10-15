package swp391.code.swp391.utils;

import swp391.code.swp391.entity.Subscription;

public class PaymentCalculationUtil {
    
    /**
     * Calculate total payment amount for a charging session
     * @param powerConsumed Power consumed in kWh
     * @param basePrice Base price per kWh
     * @param priceFactor Time-based price multiplier (e.g., 1.5 for peak hours)
     * @param subscriptionType User's subscription type
     * @param additionalFees Sum of all additional fees (penalties, etc.)
     * @return Total amount to be paid
     */
    public static double calculateTotalAmount(Double powerConsumed, 
                                             Double basePrice, 
                                             Double priceFactor, 
                                             Subscription.Type subscriptionType,
                                             Double additionalFees) {
        // Base cost = powerConsumed * basePrice * priceFactor
        double baseCost = powerConsumed * basePrice * priceFactor;
        
        // Apply subscription discount
        double discount = getSubscriptionDiscount(subscriptionType);
        double discountedCost = baseCost * (1 - discount);
        
        // Add additional fees
        return discountedCost + additionalFees;
    }
    
    /**
     * Get discount percentage based on subscription type
     * @param subscriptionType User's subscription type
     * @return Discount as a decimal (e.g., 0.1 for 10%)
     */
    public static double getSubscriptionDiscount(Subscription.Type subscriptionType) {
        if (subscriptionType == null) {
            return 0.0; // No subscription = no discount
        }
        
        return switch (subscriptionType) {
            case BASIC -> 0.05;      // 5% discount
            case PLUS -> 0.10;       // 10% discount
            case PREMIUM -> 0.15;    // 15% discount
        };
    }
    
    /**
     * Calculate charging fee based on power consumed
     * @param powerConsumed Power consumed in kWh
     * @param basePrice Base price per kWh
     * @param priceFactor Time-based price multiplier
     * @return Charging fee before discount
     */
    public static double calculateChargingFee(Double powerConsumed, Double basePrice, Double priceFactor) {
        return powerConsumed * basePrice * priceFactor;
    }
}
