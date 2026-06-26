package init.upinmcse.cctvcore.event.processor;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;
import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent.RecipientInfo;
import init.upinmcse.cctvcore.dto.event.NotificationPayload;
import init.upinmcse.cctvcore.event.producer.INotificationDispatchPublisher;
import init.upinmcse.cctvcore.model.Notification;
import init.upinmcse.cctvcore.repository.NotificationRepository;
import init.upinmcse.cctvcore.service.ISubscriberService;
import init.upinmcse.cctvcore.service.IStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class IntrusionEventProcessor {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final IStorageService storageService;
    private final ISubscriberService subscriberService;
    private final INotificationDispatchPublisher notificationPublisher;

    private static final String WS_TOPIC = "/topic/intrusion-alerts";
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss")
            .withZone(ZoneOffset.UTC);

    /**
     * Chạy trong một transaction để Notification và bản ghi outbox (notification-dispatch)
     * được commit ATOMIC — nếu alert đã lưu thì dispatch chắc chắn sẽ được gửi (qua
     * {@code OutboxRelay}). Khối dispatch bắt exception nội bộ nên lỗi resolve recipient
     * không làm rollback việc lưu Notification.
     */
    @Transactional
    public void process(IntrusionEvent event) {
        Notification saved;
        String imageUrl;
        try {
            imageUrl = uploadSnapshot(event);

            saved = Notification.builder()
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

            saved = notificationRepository.save(saved);

            NotificationPayload payload = NotificationPayload.builder()
                    .id(saved.getId())
                    .eventId(saved.getEventId())
                    .cameraId(saved.getCameraId())
                    .cameraName(saved.getCameraName())
                    .zoneName(saved.getZoneName())
                    .detectedAt(saved.getDetectedAt())
                    .imageUrl(imageUrl)
                    .alertType(saved.getAlertType())
                    .personCount(saved.getPersonCount())
                    .confidence(saved.getConfidence())
                    .build();

            messagingTemplate.convertAndSend(WS_TOPIC, payload);
            log.info("Intrusion alert processed: camera={} zone={} image={}",
                    event.getCameraName(), event.getZoneName(), imageUrl);

        } catch (Exception e) {
            log.error("Critical: failed to process intrusion event camera={}: {}",
                    event.getCameraName(), e.getMessage(), e);
            return;
        }

        // ═══ NOTIFICATION DISPATCH — non-critical, isolated ═══
        try {
            List<RecipientInfo> recipients = subscriberService.resolveRecipients(event);
            if (!recipients.isEmpty()) {
                notificationPublisher.publish(buildDispatchEvent(saved, imageUrl, recipients));
                log.debug("Dispatched notification to {} recipient(s)", recipients.size());
            }
        } catch (Exception e) {
            log.warn("Non-critical: notification dispatch failed for camera={}: {}",
                    event.getCameraName(), e.getMessage());
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

    private NotificationDispatchEvent buildDispatchEvent(Notification n, String imageUrl,
                                                          List<RecipientInfo> recipients) {
        return NotificationDispatchEvent.builder()
                .eventId(n.getEventId())
                .cameraId(n.getCameraId())
                .cameraName(n.getCameraName())
                .zoneName(n.getZoneName())
                .alertType(n.getAlertType())
                .confidence(n.getConfidence())
                .personCount(n.getPersonCount())
                .detectedAt(n.getDetectedAt())
                .imageUrl(imageUrl)
                .recipients(recipients)
                .build();
    }
}
