package init.upinmcse.cctvcore.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "CCTVCameraInfo")
public class CCTVCameraInfo extends BaseEntity {
    @MongoId
    private String id;

    @Field("CAMERA_ID")
    private Long indexId;

    @Field("NAME")
    private String name;

    @Field("IP")
    private String ip;

    @Field("PORT")
    private Integer port;

    @NotNull
    @Field("USERNAME")
    private String username;

    @Field("PWD")
    private String pwd;

    @Field("STATUS")
    private CCTVStatus status;

    @NotNull
    @Field("MODE")
    private Mode mode;

    @Field("RTSP_STREAM_URL")
    private String rtspStreamUrl;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("LOCATION")
    private GeoJsonPoint location;

    @Field("LOCATION_DETAIL")
    private LocationDetail locationDetail;

    @Field("ZONES")
    private List<Zone> zones;
}
