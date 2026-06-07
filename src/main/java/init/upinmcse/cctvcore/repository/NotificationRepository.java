package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    Page<Notification> findByRead(boolean read, Pageable pageable);

    long countByRead(boolean read);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.detectedAt >= :from AND n.detectedAt < :to")
    long countByDetectedAtBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = "SELECT EXTRACT(HOUR FROM detected_at) AS hour, COUNT(*) AS count " +
                   "FROM notifications WHERE detected_at >= :from AND detected_at < :to " +
                   "GROUP BY hour ORDER BY hour", nativeQuery = true)
    List<Object[]> getHourlyBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = "SELECT TO_CHAR(detected_at AT TIME ZONE 'UTC', 'YYYY-MM-DD') AS date, COUNT(*) AS count " +
                   "FROM notifications WHERE detected_at >= :from AND detected_at < :to " +
                   "GROUP BY date ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = "SELECT camera_id, camera_name, COUNT(*) AS count " +
                   "FROM notifications WHERE detected_at >= :from AND detected_at < :to " +
                   "GROUP BY camera_id, camera_name ORDER BY count DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopCamerasBetween(@Param("from") Instant from, @Param("to") Instant to, @Param("limit") int limit);
}
