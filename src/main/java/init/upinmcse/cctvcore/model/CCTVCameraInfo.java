package init.upinmcse.cctvcore.model;

import init.upinmcse.cctvcore.model.enums.CCTVStatus;
import init.upinmcse.cctvcore.model.enums.Mode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cameras")
public class CCTVCameraInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "ip")
    private String ip;

    @Column(name = "port")
    private Integer port;

    @NotNull
    @Column(name = "username")
    private String username;

    @Column(name = "pwd")
    private String pwd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CCTVStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private Mode mode;

    @Column(name = "rtsp_stream_url")
    private String rtspStreamUrl;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Embedded
    private LocationDetail locationDetail;

    @Builder.Default
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Zone> zones = new ArrayList<>();
}
