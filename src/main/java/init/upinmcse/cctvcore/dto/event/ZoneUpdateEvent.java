package init.upinmcse.cctvcore.dto.event;

import init.upinmcse.cctvcore.model.Zone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ZoneUpdateEvent {
    private String cameraId;
    private List<Zone> zones;
}
