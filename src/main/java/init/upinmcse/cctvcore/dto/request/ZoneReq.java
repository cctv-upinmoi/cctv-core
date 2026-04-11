package init.upinmcse.cctvcore.dto.request;

import init.upinmcse.cctvcore.model.enums.ZoneType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneReq {

    @NotBlank(message = "ZONE_NAME_REQUIRED")
    String name;

    ZoneType type;

    boolean enabled;

    List<double[]> points;
}
