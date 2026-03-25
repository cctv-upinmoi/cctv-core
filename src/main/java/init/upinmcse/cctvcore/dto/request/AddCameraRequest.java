package init.upinmcse.cctvcore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddCameraRequest {

    @NotBlank(message = "CAMERA_NAME_REQUIRED")
    String name;

    @NotBlank(message = "CAMERA_IP_REQUIRED")
    String ip;

    Integer port;

    @NotBlank(message = "CAMERA_USERNAME_REQUIRED")
    String username;

    @NotBlank(message = "CAMERA_PASSWORD_REQUIRED")
    String pwd;

    @NotNull(message = "CAMERA_MODE_REQUIRED")
    String mode;

    String rtspStreamUrl;

    Double longitude;
    Double latitude;

    @Valid
    LocationDetailRequest locationDetail;

    @Valid
    List<ZoneRequest> zones;
}
