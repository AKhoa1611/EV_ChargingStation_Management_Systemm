package swp391.code.swp391.controller;

import swp391.code.swp391.DTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.entity.User;
import swp391.code.swp391.service.UserServiceImpl;

@RestController
@RequestMapping("/api/auth/user")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<User>> checkLogin(@RequestBody LoginRequestDTO loginRequestDTO) { //return user if success, status code if fail
        User user;
        try{
            user = userServiceImpl.checkLoginUser(loginRequestDTO);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(false, "Invalid username or password", null));
            }
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(false, e.getMessage(), null));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(false, e.getMessage(), null));
        }
        return ResponseEntity.ok(new APIResponse<>(true, "Login successful", user));
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<Long>> register(@RequestBody RegisterRequestDTO registerDTO) {
        try {
            Long userId = userServiceImpl.registerUser(registerDTO);
            if (userId == -1L) {
                return ResponseEntity.badRequest().body(new APIResponse<>(false, "Registration failed", userId));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "Registration successful", userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new APIResponse<>(false, e.getMessage(), -1L));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, e.getMessage(), -1L));
        }
    }
}
