package init.upinmcse.cctvcore.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyCloakError {
    // Keycloak Admin API errors: {"errorMessage": "..."}
    String errorMessage;
    // Keycloak OAuth/token errors: {"error": "...", "error_description": "..."}
    String error;
    @JsonProperty("error_description")
    String errorDescription;
}
