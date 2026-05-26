package init.upinmcse.cctvcore.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPayload {
    private String id;
    private String eventId;
    private String cameraId;
    private String cameraName;
    private String zoneName;
    private Instant detectedAt;
    private String imageUrl;
    private String alertType;
    private Integer personCount;
}