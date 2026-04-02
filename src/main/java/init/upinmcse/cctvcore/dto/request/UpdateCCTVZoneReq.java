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
public class UpdateCCTVZoneReq {
    String cameraId;

    @Valid
    List<ZoneReq> zones;
}
