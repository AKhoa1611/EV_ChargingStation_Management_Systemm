package swp391.code.swp391.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.code.swp391.entity.PriceFactor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceFactorRepository extends JpaRepository<PriceFactor, Long> {
    List<PriceFactor> findByStation_StationId(Long stationId);
    
    @Query("SELECT pf FROM PriceFactor pf WHERE pf.station.stationId = :stationId " +
           "AND :currentTime BETWEEN pf.startTime AND pf.endTime")
    Optional<PriceFactor> findActiveFactorForStation(@Param("stationId") Long stationId, 
                                                      @Param("currentTime") LocalDateTime currentTime);
}
