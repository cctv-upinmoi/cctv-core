package init.upinmcse.cctvcore.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobRequest {
    private String name;
    private String cameraId;
    private String cameraName;
    private List<String> alertTypes = new ArrayList<>();
    private boolean enabled = true;
}
