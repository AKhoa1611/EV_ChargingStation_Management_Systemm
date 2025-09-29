package swp391.code.swp391.controller;

import swp391.code.swp391.DTO.LoginRequestDTO;
import swp391.code.swp391.DTO.LoginResponseDTO;
import swp391.code.swp391.DTO.RegisterRequestDTO;
import swp391.code.swp391.DTO.RegisterResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.code.swp391.entity.User;
import swp391.code.swp391.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> checkLogin(@RequestBody LoginRequestDTO loginRequestDTO) { //return user if success, status code if fail
        User user;
        try{
            user = userService.checkLoginUser(loginRequestDTO);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO("Invalid credentials"));
            }
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new LoginResponseDTO(e.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new LoginResponseDTO("Internal server error: "+e.getMessage()));
        }
        return ResponseEntity.ok(new LoginResponseDTO(user,null));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerDTO) {
        try {
            Long userId = userService.registerUser(registerDTO);
            if (userId == -1L) {
                return ResponseEntity.badRequest().body(new RegisterResponseDTO(-1L, "Invalid input"));
            }
            return ResponseEntity.ok(new RegisterResponseDTO(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RegisterResponseDTO(-1L, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponseDTO(-1L, "Internal server error"));
        }
    }
}
