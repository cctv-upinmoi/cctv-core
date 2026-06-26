package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;
import init.upinmcse.cctvcore.service.impl.OutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Ghi notification-dispatch event vào outbox (KHÔNG gửi thẳng Kafka). Được gọi trong
 * {@code @Transactional IntrusionEventProcessor.process}, nên bản ghi outbox commit
 * atomic cùng Notification; {@code OutboxRelay} publish lên Kafka sau đó.
 */
@Component
@Slf4j
public class KafkaNotificationDispatchPublisher implements INotificationDispatchPublisher {

    private static final String AGGREGATE_TYPE = "notification";

    private final OutboxService outboxService;
    private final String topic;

    public KafkaNotificationDispatchPublisher(
            OutboxService outboxService,
            @Value("${kafka.topics.notification-dispatch}") String topic) {
        this.outboxService = outboxService;
        this.topic = topic;
    }

    @Override
    public void publish(NotificationDispatchEvent event) {
        log.debug("Enqueueing notification dispatch to outbox topic={} eventId={} recipients={}",
                topic, event.getEventId(), event.getRecipients().size());
        outboxService.publish(topic, AGGREGATE_TYPE, event.getEventId(), event);
    }
}
