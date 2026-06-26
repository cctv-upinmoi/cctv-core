package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    /**
     * Lấy batch các event PENDING và khóa hàng (FOR UPDATE) để relay xử lý.
     * SKIP LOCKED đảm bảo nhiều instance / nhiều luồng relay không xử lý trùng:
     * instance khác sẽ bỏ qua hàng đang bị khóa thay vì chờ.
     * Phải gọi trong một transaction để giữ khóa tới khi cập nhật status xong.
     */
    @Query(value = "SELECT * FROM outbox_event WHERE status = 'PENDING' " +
                   "ORDER BY created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    List<OutboxEvent> lockPendingBatch(@Param("limit") int limit);

    /** Dọn các event đã publish cũ hơn mốc thời gian cho trước. */
    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = init.upinmcse.cctvcore.model.enums.OutboxStatus.PUBLISHED " +
           "AND o.publishedAt < :before")
    int deletePublishedBefore(@Param("before") Instant before);
}
