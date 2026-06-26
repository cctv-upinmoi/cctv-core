package init.upinmcse.cctvcore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToggleZoneEnabledReq {

    @NotNull(message = "ZONE_ENABLED_REQUIRED")
    Boolean enabled;
}
