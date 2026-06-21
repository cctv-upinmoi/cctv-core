package init.upinmcse.cctvcore.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDispatchEvent {

    private String eventId;
    private String cameraId;
    private String cameraName;
    private String zoneName;
    private String alertType;
    private Double confidence;
    private Integer personCount;
    private Instant detectedAt;
    private String imageUrl;
    private List<RecipientInfo> recipients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipientInfo {
        private String userId;
        private String email;
        private String telegramChatId;
        private List<String> channels;
    }
}
