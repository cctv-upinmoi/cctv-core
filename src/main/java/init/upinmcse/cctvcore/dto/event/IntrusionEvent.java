package init.upinmcse.cctvcore.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrusionEvent {

    /** UUID do AI service tạo ra, dùng để deduplicate */
    private String eventId;

    /** ID camera trong MongoDB */
    private String cameraId;

    /** Tên camera */
    private String cameraName;

    /** Tên zone bị xâm nhập */
    private String zoneName;

    /** Loại zone: INTRUSION | LOITERING | LINE_CROSSING */
    private String zoneType;

    /** Thời điểm phát hiện (UTC) */
    private Instant detectedAt;

    /** Độ tin cậy của YOLO (0.0 – 1.0) */
    private Double confidence;

    /** Tâm bounding box người bị phát hiện: {"x": 520, "y": 340} */
    private Map<String, Integer> centroid;
}