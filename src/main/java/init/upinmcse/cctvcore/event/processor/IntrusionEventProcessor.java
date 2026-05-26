package init.upinmcse.cctvcore.event.processor;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationPayload;
import init.upinmcse.cctvcore.model.Notification;
import init.upinmcse.cctvcore.repository.NotificationRepository;
import init.upinmcse.cctvcore.service.IStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class IntrusionEventProcessor {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final IStorageService storageService;

    private static final String WS_TOPIC = "/topic/intrusion-alerts";
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss")
            .withZone(ZoneOffset.UTC);

    public void process(IntrusionEvent event) {
        try {
            String imageUrl = uploadSnapshot(event);

            Notification notification = Notification.builder()
                    .eventId(event.getEventId())
                    .cameraId(event.getCameraId())
                    .cameraName(event.getCameraName())
                    .zoneName(event.getZoneName())
                    .confidence(event.getConfidence())
                    .detectedAt(event.getDetectedAt())
                    .imageUrl(imageUrl)
                    .alertType(event.getAlertType() != null ? event.getAlertType() : "INTRUSION")
                    .personCount(event.getPersonCount() != null ? event.getPersonCount() : 1)
                    .build();

            notification = notificationRepository.save(notification);
            log.info("Saved notification id={} camera={}", notification.getId(), notification.getCameraName());

            NotificationPayload payload = NotificationPayload.builder()
                    .id(notification.getId())
                    .cameraId(notification.getCameraId())
                    .cameraName(notification.getCameraName())
                    .zoneName(notification.getZoneName())
                    .detectedAt(notification.getDetectedAt())
                    .imageUrl(imageUrl)
                    .alertType(notification.getAlertType())
                    .personCount(notification.getPersonCount())
                    .build();

            messagingTemplate.convertAndSend(WS_TOPIC, payload);
            log.info("Intrusion alert processed: camera={} zone={} image={}",
                    event.getCameraName(), event.getZoneName(), imageUrl);

        } catch (Exception e) {
            log.error("Failed to process intrusion event: {}", e.getMessage(), e);
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
