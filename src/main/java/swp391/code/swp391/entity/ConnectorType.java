package swp391.code.swp391.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "connector_types")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConnectorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connector_type_id")
    private Long connectorTypeId;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "power_output")
    private Double powerOutput;

    @Column(name = "price_per_kwh")
    private Double pricePerKwh;

    @ManyToMany(mappedBy = "connectorTypes")
    @JsonBackReference(value = "vehicle-connectorType")
    private List<Vehicle> vehicles;

    @ManyToOne
    @JsonBackReference(value = "chargingPoint-connectorType")
    private ChargingPoint chargingPoint;
}
