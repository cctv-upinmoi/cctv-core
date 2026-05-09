package init.upinmcse.cctvcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyCountRes {
    private int hour;
    private long count;
}
