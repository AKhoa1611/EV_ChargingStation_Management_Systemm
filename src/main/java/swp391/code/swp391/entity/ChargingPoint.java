package swp391.code.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="charging_points")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingPoint {
    @Id
    private Long chargingPointId;

    @ManyToOne
    private ChargingStation station;

    @OneToMany(mappedBy = "chargingPoint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConnectorType> connectorTypes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChargingPointStatus status = ChargingPointStatus.AVAILABLE;

    public enum ChargingPointStatus {
        AVAILABLE,
        OCCUPIED,
        OUT_OF_SERVICE
    }
}
