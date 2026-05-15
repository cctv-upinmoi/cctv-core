package init.upinmcse.cctvcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRes {
    private String id;
    private String cameraId;
    private String cameraName;
    private String zoneName;
    private Instant detectedAt;
    private String imageUrl;
    private boolean read;
    private String alertType;
    private Integer personCount;
    private Date createdAt;
}
