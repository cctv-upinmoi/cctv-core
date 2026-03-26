package init.upinmcse.cctvcore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CCTVRes {
    String id;
    Long indexId;
    String name;
    String ip;
    Integer port;
    String username;
    String status;
    String mode;
    String rtspStreamUrl;
    Double longitude;
    Double latitude;
    LocationDetailRes locationDetail;
    List<ZoneRes> zones;
    Date createdAt;
    Date updatedAt;
}
