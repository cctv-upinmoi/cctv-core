package init.upinmcse.cctvcore.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneRes {
    String name;
    String type;
    boolean enabled;
    List<double[]> points;
}
