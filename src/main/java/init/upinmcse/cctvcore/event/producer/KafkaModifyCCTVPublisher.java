package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaModifyCCTVPublisher implements IModifyCCTVPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaModifyCCTVPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.modify-cctv}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(ZoneUpdateEvent event) {
        log.debug("Publishing zone update to Kafka topic={} cameraId={}", topic, event.getCameraId());
        kafkaTemplate.send(topic, event.getCameraId(), event);
    }
}
