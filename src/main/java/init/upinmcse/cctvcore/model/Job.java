package init.upinmcse.cctvcore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "camera_name")
    private String cameraName;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alert_types", columnDefinition = "TEXT")
    private List<String> alertTypes = new ArrayList<>();

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;
}
