package init.upinmcse.cctvcore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CCTVUserInfoRes {
    String userId;
    String email;
    String firstName;
    String lastName;
    List<String> roles;
}
