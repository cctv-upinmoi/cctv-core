package init.upinmcse.cctvcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraRankRes {
    private String cameraId;
    private String cameraName;
    private long count;
}
