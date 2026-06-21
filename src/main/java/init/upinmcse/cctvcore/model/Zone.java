package init.upinmcse.cctvcore.model;

import init.upinmcse.cctvcore.model.enums.ZoneType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "camera")
@EqualsAndHashCode(exclude = "camera")
@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    private CCTVCameraInfo camera;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private boolean enabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "points", columnDefinition = "jsonb")
    private List<double[]> points;
}
