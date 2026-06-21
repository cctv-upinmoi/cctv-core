package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaNotificationDispatchPublisher implements INotificationDispatchPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaNotificationDispatchPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.notification-dispatch}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(NotificationDispatchEvent event) {
        log.debug("Publishing notification dispatch to Kafka topic={} eventId={} recipients={}",
                topic, event.getEventId(), event.getRecipients().size());
        kafkaTemplate.send(topic, event.getEventId(), event);
    }
}
