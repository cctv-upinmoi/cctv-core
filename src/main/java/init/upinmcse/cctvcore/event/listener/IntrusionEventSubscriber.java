package init.upinmcse.cctvcore.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.event.processor.IntrusionEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.provider", havingValue = "redis", matchIfMissing = true)
public class IntrusionEventSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final IntrusionEventProcessor processor;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            IntrusionEvent event = objectMapper.readValue(message.getBody(), IntrusionEvent.class);
            processor.process(event);
        } catch (Exception e) {
            log.error("Failed to deserialize intrusion event from Redis: {}", e.getMessage(), e);
        }
    }
}
