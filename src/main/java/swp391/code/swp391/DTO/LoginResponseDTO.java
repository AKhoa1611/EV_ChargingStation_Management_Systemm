package swp391.code.swp391.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp391.code.swp391.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {


    private User user;
    private String error;

    public LoginResponseDTO(String error) {
        this.error = error;
    }
}
