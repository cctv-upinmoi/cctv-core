package init.upinmcse.cctvcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "camera_name")
    private String cameraName;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "detected_at")
    private Instant detectedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "alert_type")
    private String alertType;

    @Column(name = "person_count")
    private Integer personCount;

    @Column(name = "event_id", unique = true)
    private String eventId;

    @Builder.Default
    @Column(name = "read")
    private boolean read = false;
}
