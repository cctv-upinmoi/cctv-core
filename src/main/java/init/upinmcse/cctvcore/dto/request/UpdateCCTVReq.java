package init.upinmcse.cctvcore.dto.request;

import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCCTVReq {
    String cameraId;
    String name;
    String ip;
    Integer port;
    String username;
    String pwd;
    String mode;
    String rtspStreamUrl;
    String status;
    Double longitude;
    Double latitude;

    @Valid
    LocationDetailReq locationDetail;

    @Valid
    List<ZoneReq> zones;
}
