package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;
import init.upinmcse.cctvcore.service.impl.OutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Ghi zone-update event vào outbox (KHÔNG gửi thẳng Kafka). Vì được gọi trong
 * {@code @Transactional CCTVService.updateCCTVZone}, bản ghi outbox commit atomic
 * cùng thay đổi zone; {@code OutboxRelay} sẽ publish lên Kafka sau đó.
 */
@Component
@Slf4j
public class KafkaModifyCCTVPublisher implements IModifyCCTVPublisher {

    private static final String AGGREGATE_TYPE = "zone";

    private final OutboxService outboxService;
    private final String topic;

    public KafkaModifyCCTVPublisher(
            OutboxService outboxService,
            @Value("${kafka.topics.modify-cctv}") String topic) {
        this.outboxService = outboxService;
        this.topic = topic;
    }

    @Override
    public void publish(ZoneUpdateEvent event) {
        log.debug("Enqueueing zone update to outbox topic={} cameraId={}", topic, event.getCameraId());
        outboxService.publish(topic, AGGREGATE_TYPE, event.getCameraId(), event);
    }
}
