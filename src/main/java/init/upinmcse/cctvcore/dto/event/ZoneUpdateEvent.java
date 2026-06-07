package init.upinmcse.cctvcore.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ZoneUpdateEvent {

    private String cameraId;
    private List<ZoneDto> zones;

    @Getter
    @Builder
    public static class ZoneDto {
        private String name;
        private boolean enabled;
        private List<double[]> points;
    }
}
