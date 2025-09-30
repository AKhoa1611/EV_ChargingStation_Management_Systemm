package swp391.code.swp391.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import swp391.code.swp391.dto.LoginRequestDTO;
import swp391.code.swp391.dto.RegisterRequestDTO;
import org.springframework.stereotype.Service;
import swp391.code.swp391.dto.UpdateUserDTO;
import swp391.code.swp391.entity.User;
import swp391.code.swp391.entity.Vehicle;
import swp391.code.swp391.repository.UserRepository;
import swp391.code.swp391.repository.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    // Regular expressions for validation
    /** Pattern for validating email addresses */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    /** Pattern for validating Vietnamese phone numbers */
    private static final String VIETNAM_PHONE_REGEX = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$";

    // Dependencies
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    // Lưu trữ mã xác thực tạm thời (trong thực tế nên dùng Redis)
    private final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();
    @Autowired
    private VehicleRepository vehicleRepository;


    /**
     * Constructor injection for dependencies
     * Initializes BCryptPasswordEncoder for password hashing
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    /**
     * Authenticates user login using either email or phone
     * @param loginRequestDTO Contains username (email/phone) and password
     * @return User object if authentication successful, null otherwise
     * @throws IllegalArgumentException if username format is invalid
     */
    @Override
    public User checkLoginUser(LoginRequestDTO loginRequestDTO) {
        User user;
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();

        // Validate username format
        if (!isValidEmail(username) && !isValidVietnamPhone(username)) {
            throw new IllegalArgumentException("Invalid email or phone number");
        }

        // Attempt login based on username type
        if (isValidEmail(username)) {
            user = checkLoginByEmail(username, password);
        } else {
            user = checkLoginByPhone(username, password);
        }
        return user;
    }

    /**
     * Registers a new user in the system
     * @param registerDTO Contains user registration details
     * @return User ID if registration successful, -1 if failed
     * @throws IllegalArgumentException if email already exists
     */
    @Override
    public Long registerUser(RegisterRequestDTO registerDTO) {
        String username = registerDTO.getEmail();
        // Check for existing email
        if (checkUserExistsByEmail(username)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash password and create new user
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        User user = new User(registerDTO.getFullName(), username, encodedPassword, null, null, null);

        try {
            return addUser(user);
        } catch (Exception e) {
//            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Authenticates user login using email
     * @return User object if authentication successful, null otherwise
     */
    @Override
    public User checkLoginByEmail(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verify password hash matches
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    // Similar authentication method for phone login
    @Override
    public User checkLoginByPhone(String phone, String password) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Validates email format using regex pattern
     */
    @Override
    public boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Validates Vietnamese phone number format using regex pattern
     */
    @Override
    public boolean isValidVietnamPhone(String phone) {
        return phone != null && Pattern.matches(VIETNAM_PHONE_REGEX, phone);
    }

    /**
     * Saves a new user and returns their ID
     */
    @Override
    public Long addUser(User user) {
        return userRepository.save(user).getUserId();
    }

    // User existence check methods
    @Override
    public boolean checkUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkUserExistsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }




    /**
     * 1. XEM THÔNG TIN USER
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }



    /**
     * 2. CẬP NHẬT THÔNG TIN CƠ BẢN (không cần xác thực)
     */
    public User updateUserProfile(Long userId, UpdateUserDTO updateDTO) {
        User user = getUserById(userId);

        // Cập nhật các thông tin an toàn
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }

        if (updateDTO.getAddress() != null) {
            user.setAddress(updateDTO.getAddress());
        }

        if (updateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDTO.getDateOfBirth());
        }
        // Xử lý thay đổi mật khẩu
        if (updateDTO.getOldPassword() != null && updateDTO.getNewPassword() != null
                && updateDTO.getConfirmNewPassword() != null) {
            // Kiểm tra xem mật khẩu mới và nhập lại mật khẩu mới có khớp nhau không
            if (!updateDTO.getNewPassword().equals(updateDTO.getConfirmNewPassword())) {
                throw new RuntimeException("Mật khẩu mới và nhập lại mật khẩu mới không khớp");
            }

            // Kiểm tra mật khẩu cũ
            if (!passwordEncoder.matches(updateDTO.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }

            // Mã hóa và cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * 3A. GỬI MÃ XÁC THỰC EMAIL
     */
    public String sendEmailVerification(Long userId, String newEmail) {
        // Kiểm tra user tồn tại
        User user = getUserById(userId);

        // Kiểm tra email mới có hợp lệ không
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email mới không được để trống");
        }

        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email này đã được sử dụng bởi user khác");
        }

        // Kiểm tra không trùng với email hiện tại
        if (newEmail.equals(user.getEmail())) {
            throw new IllegalArgumentException("Email mới không được trùng với email hiện tại");
        }

        // Tạo mã xác thực
        String verificationCode = generateVerificationCode();

        // Lưu mã xác thực (key = userId_email_type)
        String key = userId + "_" + newEmail + "_EMAIL";
        verificationCodes.put(key, new VerificationData(
                verificationCode,
                newEmail,
                LocalDateTime.now().plusMinutes(15) // Hết hạn sau 15 phút
        ));

        // Gửi email (giả lập)
        sendEmailCode(newEmail, verificationCode, user.getFullName());

        return "Mã xác thực đã được gửi đến: " + maskEmail(newEmail);
    }

    /**
     * 3B. XÁC THỰC VÀ CẬP NHẬT EMAIL
     */
    public User confirmEmailChange(Long userId, String newEmail, String verificationCode) {
        User user = getUserById(userId);

        // Kiểm tra mã xác thực
        String key = userId + "_" + newEmail + "_EMAIL";
        VerificationData verificationData = verificationCodes.get(key);

        if (verificationData == null) {
            throw new IllegalArgumentException("Không tìm thấy mã xác thực. Vui lòng gửi lại mã.");
        }

        if (verificationData.isExpired()) {
            verificationCodes.remove(key);
            throw new IllegalArgumentException("Mã xác thực đã hết hạn. Vui lòng gửi lại mã.");
        }

        if (!verificationData.getCode().equals(verificationCode)) {
            throw new IllegalArgumentException("Mã xác thực không đúng");
        }

        // Kiểm tra email lần nữa (phòng trường hợp có người khác dùng trong lúc chờ)
        if (userRepository.existsByEmail(newEmail)) {
            verificationCodes.remove(key);
            throw new IllegalArgumentException("Email này đã được sử dụng bởi user khác");
        }

        // Cập nhật email
        user.setEmail(newEmail);
        User savedUser = userRepository.save(user);

        // Xóa mã xác thực đã sử dụng
        verificationCodes.remove(key);

        return savedUser;
    }

    /**
     * 4A. GỬI MÃ XÁC THỰC SỐ ĐIỆN THOẠI
     */
    public String sendPhoneVerification(Long userId, String newPhone) {
        // Kiểm tra user tồn tại
        User user = getUserById(userId);

        // Kiểm tra số điện thoại hợp lệ
        if (newPhone == null || !isValidPhoneNumber(newPhone)) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }

        // Kiểm tra số điện thoại đã tồn tại chưa
        if (userRepository.existsByPhone(newPhone)) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng bởi user khác");
        }

        // Kiểm tra không trùng với phone hiện tại
        if (newPhone.equals(user.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại mới không được trùng với số hiện tại");
        }

        // Tạo mã xác thực
        String verificationCode = generateVerificationCode();

        // Lưu mã xác thực
        String key = userId + "_" + newPhone + "_PHONE";
        verificationCodes.put(key, new VerificationData(
                verificationCode,
                newPhone,
                LocalDateTime.now().plusMinutes(15)
        ));

        // Gửi SMS (giả lập)
        sendSMSCode(newPhone, verificationCode, user.getFullName());

        return "Mã xác thực đã được gửi đến: " + maskPhoneNumber(newPhone);
    }

    /**
     * 4B. XÁC THỰC VÀ CẬP NHẬT SỐ ĐIỆN THOẠI
     */
    public User confirmPhoneChange(Long userId, String newPhone, String verificationCode) {
        User user = getUserById(userId);

        // Kiểm tra mã xác thực
        String key = userId + "_" + newPhone + "_PHONE";
        VerificationData verificationData = verificationCodes.get(key);

        if (verificationData == null) {
            throw new IllegalArgumentException("Không tìm thấy mã xác thực. Vui lòng gửi lại mã.");
        }

        if (verificationData.isExpired()) {
            verificationCodes.remove(key);
            throw new IllegalArgumentException("Mã xác thực đã hết hạn. Vui lòng gửi lại mã.");
        }

        if (!verificationData.getCode().equals(verificationCode)) {
            throw new IllegalArgumentException("Mã xác thực không đúng");
        }

        // Kiểm tra phone lần nữa
        if (userRepository.existsByPhone(newPhone)) {
            verificationCodes.remove(key);
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng bởi user khác");
        }

        // Cập nhật số điện thoại
        user.setPhone(newPhone);
        User savedUser = userRepository.save(user);

        // Xóa mã xác thực đã sử dụng
        verificationCodes.remove(key);

        return savedUser;
    }

    /**
     * 5. THAY ĐỔI TRẠNG THÁI USER (admin only)
     */
    public User changeUserStatus(Long userId, User.UserStatus newStatus) {
        User user = getUserById(userId);
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public User banUser(Long id) {
        return null;
    }

    // =============== HELPER METHODS ===============

    /**
     * Tạo mã xác thực 6 số ngẫu nhiên
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

//    /**
//     * Kiểm tra email hợp lệ
//     */
//    private boolean isValidEmail(String email) {
//        return email != null &&
//                email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
//    }

    /**
     * Kiểm tra số điện thoại hợp lệ (Việt Nam)
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null &&
                phone.matches("^(03|05|07|08|09)[0-9]{8}$");
    }

    /**
     * Ẩn một phần email để bảo mật
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) return email;

        String maskedUsername = username.charAt(0) +
                "*".repeat(username.length() - 2) +
                username.charAt(username.length() - 1);

        return maskedUsername + "@" + domain;
    }

    /**
     * Ẩn một phần số điện thoại để bảo mật
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) return phone;

        return phone.substring(0, 3) +
                "*".repeat(phone.length() - 6) +
                phone.substring(phone.length() - 3);
    }

    /**
     * Giả lập gửi email
     */
    private void sendEmailCode(String email, String code, String fullName) {
        System.out.println("=== SENDING EMAIL ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Mã xác thực thay đổi email");
        System.out.println("Content: Xin chào " + fullName + ",");
        System.out.println("Mã xác thực của bạn là: " + code);
        System.out.println("Mã có hiệu lực trong 15 phút.");
        System.out.println("====================");

        // TODO: Implement real email sending
        // emailService.sendVerificationCode(email, code, fullName);
    }

    /**
     * Giả lập gửi SMS
     */
    private void sendSMSCode(String phone, String code, String fullName) {
        System.out.println("=== SENDING SMS ===");
        System.out.println("To: " + phone);
        System.out.println("Message: Xin chào " + fullName + ". Mã xác thực của bạn là: " + code + ". Có hiệu lực 15 phút.");
        System.out.println("===================");

        // TODO: Implement real SMS sending
        // smsService.sendVerificationCode(phone, code, fullName);
    }

    // =============== INNER CLASSES ===============

    /**
     * Class lưu trữ thông tin mã xác thực
     */
    private static class VerificationData {
        @Getter
        private final String code;
        @Getter
        private final String newValue;
        private final LocalDateTime expiryTime;

        public VerificationData(String code, String newValue, LocalDateTime expiryTime) {
            this.code = code;
            this.newValue = newValue;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }



}