package swp391.code.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "charging_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStation {
    @Id
    private Long stationId;
    @Column(name = "station_name", nullable = false)
    private String stationName;
    @Column(name = "address", nullable = false)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChargingStationStatus status = ChargingStationStatus.ACTIVE;

    private double latitude; //Vi tri vi do
    private double longitude; //Vi tri kinh do
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChargingPoint> chargingPoint;

    public enum ChargingStationStatus {
        ACTIVE,
        INACTIVE,
        MAINTENANCE
    }
    @Column(name = "chargingPointNumber", nullable = false)
    private int chargingPointNumber;
}
