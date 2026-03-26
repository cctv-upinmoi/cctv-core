package init.upinmcse.cctvcore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationReq {
    @Email(message = "")
    String email;
    String firstName;
    String lastName;
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
}
