package init.upinmcse.cctvcore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntrusionEventSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String WS_TOPIC = "/topic/intrusion-alerts";

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            IntrusionEvent event = objectMapper.readValue(message.getBody(), IntrusionEvent.class);

            // create notify record

            // save db

            // push notify to ui
            messagingTemplate.convertAndSend(WS_TOPIC, event);
        } catch (Exception e) {
            log.error("Failed to process intrusion event from Redis: {}", e.getMessage());
        }
    }
}