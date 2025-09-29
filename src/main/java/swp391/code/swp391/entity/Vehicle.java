package swp391.code.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "vehicles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vehicle {

    @Id
    @Column(name = "plate_number")
    private String plateNumber;
    @Column(name = "brand", nullable = false)
    private String brand;
    @Column(name = "model", nullable = false)
    private String model;
    @Column(name = "capacity", nullable = false)
    private double capacity;
    @Column(name = "product_year", nullable = false)
    private int productYear;

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(
        name = "vehicle_connector_types",
        joinColumns = @JoinColumn(name = "plateNumber"),
        inverseJoinColumns = @JoinColumn(name = "connector_type_id")
    )
    private List<ConnectorType> connectorTypes;
}
