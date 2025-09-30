package swp391.code.swp391.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.sql.Date;

/**
 * DTO cho việc cập nhật thông tin cơ bản của user và thay đổi mật khẩu
 * Chỉ bao gồm những thông tin an toàn, không cần xác thực
 */
public class UpdateUserDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    private String fullName;

    @Size(max = 500, message = "Địa chỉ không quá 500 ký tự")
    private String address;

    /**
     * Ngày sinh phải là ngày trong quá khứ (nếu có)
     * Có thể null nếu người dùng không muốn cung cấp
     */
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private Date dateOfBirth;

    /**
     * Mật khẩu cũ để xác thực danh tính người dùng
     * Chỉ cần thiết khi thay đổi mật khẩu
     */
    @Size(min = 6, max = 100, message = "Mật khẩu cũ phải từ 6-100 ký tự")
    private String oldPassword;

    /**
     * Mật khẩu mới để thay đổi
     * Chỉ cần thiết khi thay đổi mật khẩu
     */
    @Size(min = 6, max = 100, message = "Mật khẩu mới phải từ 6-100 ký tự")
    private String newPassword;

    /**
     * Nhập lại mật khẩu mới để xác nhận
     * Chỉ cần thiết khi thay đổi mật khẩu
     */
    @Size(min = 6, max = 100, message = "Nhập lại mật khẩu mới phải từ 6-100 ký tự")
    private String confirmNewPassword;

    // Constructor rỗng
    public UpdateUserDTO() {}

    // Constructor đầy đủ
    public UpdateUserDTO(String fullName, String address, Date dateOfBirth,
                         String oldPassword, String newPassword, String confirmNewPassword) {
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    // Getters và Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    @Override
    public String toString() {
        return "UpdateUserDTO{" +
                "fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", oldPassword='[REDACTED]'" +
                ", newPassword='[REDACTED]'" +
                ", confirmNewPassword='[REDACTED]'" +
                '}';
    }
}