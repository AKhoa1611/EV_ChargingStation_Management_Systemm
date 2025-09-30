package swp391.code.swp391.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private Long userId;
    private String error; // null if no error, otherwise contains error message

    public RegisterResponseDTO(Long userId) {
        this.userId = userId;
        this.error = null;
    }

}
