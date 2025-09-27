package swp391.code.swp391.Controller;

import DTO.LoginRequestDTO;
import DTO.RegisterRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.Entity.User;
import swp391.code.swp391.Service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> checkLogin(@RequestBody LoginRequestDTO loginRequestDTO) { //return user if success, response status code and null if fail
        User user = null;
        try{
            user = userService.checkLoginUser(loginRequestDTO);
            if (user == null) {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody RegisterRequestDTO registerDTO) {
        try {
            Long userId = userService.registerUser(registerDTO);
            if (userId == -1L) {
                return ResponseEntity.badRequest().body(-1L);
            }
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(-1L);
        }
    }
}
