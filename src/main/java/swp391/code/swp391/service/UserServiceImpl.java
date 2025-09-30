package swp391.code.swp391.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import swp391.code.swp391.DTO.LoginRequestDTO;
import swp391.code.swp391.DTO.RegisterRequestDTO;
import org.springframework.stereotype.Service;
import swp391.code.swp391.entity.User;
import swp391.code.swp391.repository.UserRepository;

import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl implements UserService {
    // Regular expressions for validation
    /** Pattern for validating email addresses */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    /** Pattern for validating Vietnamese phone numbers */
    private static final String VIETNAM_PHONE_REGEX = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$";

    // Dependencies
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Constructor injection for dependencies
     * Initializes BCryptPasswordEncoder for password hashing
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticates user login using either email or phone
     * @param loginRequestDTO Contains username (email/phone) and password
     * @return User object if authentication successful, null otherwise
     * @throws IllegalArgumentException if username format is invalid
     */
    @Override
    public User checkLoginUser(LoginRequestDTO loginRequestDTO) {
        User user = null;
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
            e.printStackTrace();
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
}