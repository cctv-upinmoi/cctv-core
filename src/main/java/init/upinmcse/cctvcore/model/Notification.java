package init.upinmcse.cctvcore.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "CCTVNotification")
public class Notification extends BaseEntity {

    @Id
    private String id;

    @Field("camera_id")
    private String cameraId;

    @Field("camera_name")
    private String cameraName;

    @Field("zone_name")
    private String zoneName;

    @Field("confidence")
    private Double confidence;

    @Field("detected_at")
    private Instant detectedAt;

    /** Relative URL served by Spring, e.g. /snapshots/cam1/Zone_A_20260501_103000.jpg */
    @Field("image_url")
    private String imageUrl;
}
