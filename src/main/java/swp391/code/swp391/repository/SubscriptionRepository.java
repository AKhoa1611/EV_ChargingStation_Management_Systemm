package swp391.code.swp391.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.code.swp391.entity.Subscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser_UserId(Long userId);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId " +
           "AND :currentTime BETWEEN s.startDate AND s.endDate")
    Optional<Subscription> findActiveSubscriptionForUser(@Param("userId") Long userId, 
                                                          @Param("currentTime") LocalDateTime currentTime);
}
