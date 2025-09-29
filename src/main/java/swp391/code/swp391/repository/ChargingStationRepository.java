package swp391.code.swp391.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.code.swp391.entity.ChargingStation;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    List<ChargingStation> findByStationNameContainingIgnoreCase(String name);
    List<ChargingStation> findByStatus(ChargingStation.ChargingStationStatus status);
}
