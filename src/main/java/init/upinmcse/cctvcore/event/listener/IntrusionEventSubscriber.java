package init.upinmcse.cctvcore.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationPayload;
import init.upinmcse.cctvcore.model.Notification;
import init.upinmcse.cctvcore.repository.NotificationRepository;
import init.upinmcse.cctvcore.service.IStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class IntrusionEventSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final IStorageService storageService;

    private static final String WS_TOPIC = "/topic/intrusion-alerts";
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss")
            .withZone(ZoneOffset.UTC);

    @Value("${redis.channels.intrusion-events}")
    private String intrusionEventChannel;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            IntrusionEvent event = objectMapper.readValue(message.getBody(), IntrusionEvent.class);

            String imageUrl = uploadSnapshot(event);

            Notification notification = Notification.builder()
                    .cameraId(event.getCameraId())
                    .cameraName(event.getCameraName())
                    .zoneName(event.getZoneName())
                    .confidence(event.getConfidence())
                    .detectedAt(event.getDetectedAt())
                    .imageUrl(imageUrl)
                    .build();

            log.info("database: {}",notification.getCameraName());
//            notificationRepository.save(notification);

            NotificationPayload payload = NotificationPayload.builder()
                    .id(notification.getId())
                    .cameraId(notification.getCameraId())
                    .cameraName(notification.getCameraName())
                    .zoneName(notification.getZoneName())
                    .confidence(notification.getConfidence())
                    .detectedAt(notification.getDetectedAt())
                    .imageUrl(imageUrl)
                    .build();

//            messagingTemplate.convertAndSend(WS_TOPIC, payload);
            log.info("websocket: {}", payload.getCameraName());
            log.info("Intrusion alert processed: camera={} zone={} image={}",
                    event.getCameraName(), event.getZoneName(), imageUrl);

        } catch (Exception e) {
            log.error("Failed to process intrusion event from Redis: {}", e.getMessage(), e);
        }
    }

    private String uploadSnapshot(IntrusionEvent event) {
        String ts       = FILE_TS.format(event.getDetectedAt());
        String safeCam  = event.getCameraName().replaceAll("[^a-zA-Z0-9_-]", "_");
        String safeZone = event.getZoneName().replaceAll("[^a-zA-Z0-9_-]", "_");
        String key      = "snapshots/" + safeCam + "/" + safeZone + "_" + ts + ".jpg";

        byte[] imageBytes = Base64.getDecoder().decode(event.getImage());
        return storageService.upload(key, imageBytes, "image/jpeg");
    }
}