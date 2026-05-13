package init.upinmcse.cctvcore.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrusionEvent {

    @JsonProperty("camera_id")
    private String cameraId;

    @JsonProperty("camera_name")
    private String cameraName;

    @JsonProperty("zone_name")
    private String zoneName;

    @JsonProperty("confidence")
    private Double confidence;

    /** ISO-8601 timestamp từ AI service */
    @JsonProperty("timestamp")
    private Instant detectedAt;

    /** INTRUSION | PROXIMITY */
    @JsonProperty("alert_type")
    private String alertType;

    /** Number of persons detected in zone at time of alert */
    @JsonProperty("person_count")
    private Integer personCount;

    /** Base64-encoded JPEG snapshot */
    @JsonProperty("image")
    private String image;
}