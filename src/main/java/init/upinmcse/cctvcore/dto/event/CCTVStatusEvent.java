package init.upinmcse.cctvcore.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class CCTVStatusEvent {
    private String type;   // "snapshot" | "update" | "heartbeat"
    private List<CCTVStatus> cameras;

    @Data
    @AllArgsConstructor
    public static class CCTVStatus {
        private String    id;
        private String  name;
        private String  status;     // "OK" | "ERROR"
        private Instant checkedAt;
    }
}
