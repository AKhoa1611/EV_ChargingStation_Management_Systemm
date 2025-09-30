package swp391.code.swp391.service;

import swp391.code.swp391.DTO.LoginRequestDTO;
import swp391.code.swp391.DTO.RegisterRequestDTO;
import swp391.code.swp391.entity.User;

public interface UserService {
    User checkLoginUser(LoginRequestDTO loginRequestDTO);
    Long registerUser(RegisterRequestDTO registerDTO);
    User checkLoginByEmail(String email, String password);
    User checkLoginByPhone(String phone, String password);
    boolean isValidEmail(String email);
    boolean isValidVietnamPhone(String phone);
    Long addUser(User user);
    boolean checkUserExistsByEmail(String email);
    boolean checkUserExistsByPhone(String phone);
}
