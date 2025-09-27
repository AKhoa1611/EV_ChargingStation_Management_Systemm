package swp391.code.swp391.Service;

import DTO.LoginRequestDTO;
import DTO.RegisterRequestDTO;
import org.springframework.stereotype.Service;
import swp391.code.swp391.Entity.User;
import swp391.code.swp391.Repository.UserRepository;

import java.util.regex.Pattern;

@Service
public class UserService {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String VIETNAM_PHONE_REGEX = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$";
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User checkLoginUser(LoginRequestDTO loginRequestDTO) { //return user if success, null if fail
        User user = null;
        try {
            if (!isValidEmail(loginRequestDTO.getUsername()) && !isValidVietnamPhone(loginRequestDTO.getUsername())) { //Check if username is valid email or phone
                return null;
            }
            if (isValidEmail(loginRequestDTO.getUsername())) { //Check if username is email
                user = checkLoginByEmail(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
            } else if (isValidVietnamPhone(loginRequestDTO.getUsername())) {//Check if username is phone
                user = checkLoginByPhone(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public Long registerUser(RegisterRequestDTO registerDTO) {
        String username = registerDTO.getUsername();
        if (!isValidEmail(username) && !isValidVietnamPhone(username)) {
            return -1L;
        }
        if (checkUserExistsByEmail(username) || checkUserExistsByPhone(username)) {
            return -1L;
        }
        User user = new User(registerDTO.getFullName(), username, registerDTO.getPassword(), null, null, null);
        try {
            return addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public User checkLoginByEmail(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    public User checkLoginByPhone(String phone, String password){
        User user = userRepository.findByPhone(phone);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean isValidVietnamPhone(String phone) {
        return Pattern.matches(VIETNAM_PHONE_REGEX, phone);
    }

    public Long addUser(User user) {//return userId if success, -1 if fail (Register)
        return userRepository.save(user).getUserId();
    }

    public boolean checkUserExistsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean checkUserExistsByPhone(String phone) {
        return userRepository.findByPhone(phone) != null;
    }
}
