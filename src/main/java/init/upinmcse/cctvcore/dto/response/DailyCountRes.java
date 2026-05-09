package init.upinmcse.cctvcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCountRes {
    private String date;  // yyyy-MM-dd UTC
    private long count;
}
