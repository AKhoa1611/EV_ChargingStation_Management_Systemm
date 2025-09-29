package swp391.code.swp391.entity;
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

    @Column(name = "name", nullable = false)
    private String typeName;

    @ManyToMany(mappedBy = "connectorTypes")
    private List<Vehicle> vehicles;

    @ManyToOne
    private ChargingPoint chargingPoint;
}
