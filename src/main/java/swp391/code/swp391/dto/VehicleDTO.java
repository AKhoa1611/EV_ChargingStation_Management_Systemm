package swp391.code.swp391.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp391.code.swp391.entity.ConnectorType;
import swp391.code.swp391.entity.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ include field không null khi serialize
public class VehicleDTO {

    @NotBlank(message = "Plate number is required")
    @Size(max = 20, message = "Plate number must not exceed 20 characters")
    private String plateNumber;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @Positive(message = "Capacity must be positive")
    private double capacity;

    @Min(value = 1900, message = "Product year must be from 1900")
    @Max(value = 2030, message = "Product year must not exceed 2030")
    private int productYear;

    // Cho input: chỉ cần userId
    private Long userId;

    // Cho output: full User object
    private User user;

    // Cho input: list connector type IDs
    private List<Long> connectorTypeIds;

    // Cho output: full ConnectorType objects
    private List<ConnectorType> connectorTypes;
}