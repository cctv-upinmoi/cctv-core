package init.upinmcse.cctvcore.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.event.processor.IntrusionEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaIntrusionEventSubscriber {

    private final ObjectMapper objectMapper;
    private final IntrusionEventProcessor processor;

    @KafkaListener(topics = "${kafka.topics.intrusion-events}", groupId = "cctv-core")
    public void onMessage(String payload) {
        try {
            IntrusionEvent event = objectMapper.readValue(payload, IntrusionEvent.class);
            log.debug("Received intrusion event from Kafka: camera={}", event.getCameraName());
            processor.process(event);
        } catch (Exception e) {
            log.error("Failed to deserialize intrusion event from Kafka: {}", e.getMessage(), e);
        }
    }
}
