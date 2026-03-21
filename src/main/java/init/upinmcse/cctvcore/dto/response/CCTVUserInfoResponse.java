package init.upinmcse.cctvcore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CCTVUserInfoResponse {
    String userId;
    String profileId;
    String email;
}
