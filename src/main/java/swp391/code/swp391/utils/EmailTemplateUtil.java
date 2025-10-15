package swp391.code.swp391.utils;

import swp391.code.swp391.dto.InvoiceDTO;

import java.text.SimpleDateFormat;

public class EmailTemplateUtil {
    
    /**
     * Generate email invoice HTML template
     * @param invoice Invoice details
     * @return HTML email content
     */
    public static String generateInvoiceEmail(InvoiceDTO invoice) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        StringBuilder feesHtml = new StringBuilder();
        if (invoice.getFees() != null && !invoice.getFees().isEmpty()) {
            for (InvoiceDTO.FeeDetail fee : invoice.getFees()) {
                feesHtml.append("<tr>")
                       .append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'>")
                       .append(fee.getFeeType())
                       .append("</td>")
                       .append("<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>")
                       .append(String.format("%,.0f VND", fee.getAmount()))
                       .append("</td>")
                       .append("</tr>");
            }
        }
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                "table { width: 100%; border-collapse: collapse; margin: 15px 0; background-color: white; }" +
                "th { background-color: #4CAF50; color: white; padding: 12px; text-align: left; }" +
                "td { padding: 8px; border-bottom: 1px solid #ddd; }" +
                ".total { font-size: 18px; font-weight: bold; color: #4CAF50; }" +
                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Hóa Đơn Sạc Xe Điện</h1>" +
                "<p>EV Charging Station Management</p>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Thông tin khách hàng</h2>" +
                "<p><strong>Họ tên:</strong> " + invoice.getUserName() + "</p>" +
                "<p><strong>Email:</strong> " + invoice.getUserEmail() + "</p>" +
                "<h2>Thông tin trạm sạc</h2>" +
                "<p><strong>Tên trạm:</strong> " + invoice.getStationName() + "</p>" +
                "<p><strong>Địa chỉ:</strong> " + invoice.getStationAddress() + "</p>" +
                "<h2>Chi tiết giao dịch</h2>" +
                "<table>" +
                "<tr>" +
                "<th>Mô tả</th>" +
                "<th style='text-align: right;'>Giá trị</th>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Thời gian bắt đầu</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                dateFormat.format(invoice.getStartTime()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Thời gian kết thúc</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                dateFormat.format(invoice.getEndTime()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Điện năng tiêu thụ</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                String.format("%.2f kWh", invoice.getPowerConsumed()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Giá cơ bản</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                String.format("%,.0f VND/kWh", invoice.getBasePrice()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Hệ số giá</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>x" + 
                invoice.getPriceFactor() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Giảm giá thành viên</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                String.format("%.0f%%", invoice.getSubscriptionDiscount() * 100) + "</td>" +
                "</tr>" +
                feesHtml.toString() +
                "<tr>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd;'>Tạm tính</td>" +
                "<td style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>" + 
                String.format("%,.0f VND", invoice.getSubtotal()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 12px; border-bottom: 2px solid #4CAF50;'><strong>Tổng cộng</strong></td>" +
                "<td class='total' style='padding: 12px; border-bottom: 2px solid #4CAF50; text-align: right;'>" + 
                String.format("%,.0f VND", invoice.getTotalAmount()) + "</td>" +
                "</tr>" +
                "</table>" +
                "<p><strong>Phương thức thanh toán:</strong> " + invoice.getPaymentMethod() + "</p>" +
                "<p><strong>Ngày thanh toán:</strong> " + dateFormat.format(invoice.getPaymentDate()) + "</p>" +
                "<p><strong>Mã giao dịch:</strong> #" + invoice.getTransactionId() + "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                "<p>EV Charging Station Management System</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
