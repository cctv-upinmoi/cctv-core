package init.upinmcse.cctvcore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDetailResponse {
    String address;
    String ward;
    String district;
    String province;
    String description;
}
