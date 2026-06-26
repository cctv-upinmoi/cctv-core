package init.upinmcse.cctvcore.event.relay;

import init.upinmcse.cctvcore.model.OutboxEvent;
import init.upinmcse.cctvcore.model.enums.OutboxStatus;
import init.upinmcse.cctvcore.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Relay của Outbox pattern: định kỳ quét các {@link OutboxEvent} ở trạng thái PENDING
 * và publish lên Kafka, đảm bảo at-least-once — không message nào bị mất kể cả khi
 * Kafka tạm thời unavailable hay app crash sau khi commit business data.
 *
 * <p>An toàn multi-instance nhờ {@code FOR UPDATE SKIP LOCKED}: nhiều instance chạy
 * song song sẽ không xử lý trùng một bản ghi.
 */
@Component
@Slf4j
public class OutboxRelay {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final int batchSize;
    private final int maxRetries;
    private final boolean cleanupEnabled;
    private final long retentionHours;

    private static final long SEND_TIMEOUT_SECONDS = 10;

    public OutboxRelay(OutboxEventRepository outboxRepository,
                       KafkaTemplate<String, String> stringKafkaTemplate,
                       @Value("${outbox.relay.batch-size}") int batchSize,
                       @Value("${outbox.relay.max-retries}") int maxRetries,
                       @Value("${outbox.cleanup.enabled}") boolean cleanupEnabled,
                       @Value("${outbox.cleanup.retention-hours}") long retentionHours) {
        this.outboxRepository = outboxRepository;
        this.stringKafkaTemplate = stringKafkaTemplate;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
        this.cleanupEnabled = cleanupEnabled;
        this.retentionHours = retentionHours;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.poll-interval-ms}")
    @Transactional
    public void relayPendingEvents() {
        List<OutboxEvent> batch = outboxRepository.lockPendingBatch(batchSize);
        if (batch.isEmpty()) {
            return;
        }
        log.debug("Outbox relay processing {} pending event(s)", batch.size());

        for (OutboxEvent event : batch) {
            try {
                stringKafkaTemplate
                        .send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                event.setLastError(null);

            } catch (Exception e) {
                int attempts = event.getRetryCount() + 1;
                event.setRetryCount(attempts);
                event.setLastError(truncate(e.getMessage()));

                if (attempts >= maxRetries) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event {} FAILED after {} attempts (topic={} key={}): {}",
                            event.getId(), attempts, event.getTopic(), event.getAggregateId(), e.getMessage());
                } else {
                    // giữ PENDING -> sẽ thử lại ở lượt poll kế tiếp
                    log.warn("Outbox event {} publish failed (attempt {}/{}), will retry: {}",
                            event.getId(), attempts, maxRetries, e.getMessage());
                }
            }
        }
        outboxRepository.saveAll(batch);
    }

    @Scheduled(fixedDelayString = "${outbox.cleanup.interval-ms}")
    @Transactional
    public void cleanupPublished() {
        if (!cleanupEnabled) {
            return;
        }
        Instant cutoff = Instant.now().minus(retentionHours, ChronoUnit.HOURS);
        int deleted = outboxRepository.deletePublishedBefore(cutoff);
        if (deleted > 0) {
            log.info("Outbox cleanup removed {} published event(s) older than {}h", deleted, retentionHours);
        }
    }

    private static String truncate(String msg) {
        if (msg == null) return null;
        return msg.length() > 1000 ? msg.substring(0, 1000) : msg;
    }
}
