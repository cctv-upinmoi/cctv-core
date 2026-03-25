package init.upinmcse.cctvcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDetailRequest {
    @NotBlank(message = "ADDRESS_REQUIRED")
    String address;
    String ward;
    String district;
    @NotBlank(message = "PROVINCE_REQUIRED")
    String province;
    String description;
}
