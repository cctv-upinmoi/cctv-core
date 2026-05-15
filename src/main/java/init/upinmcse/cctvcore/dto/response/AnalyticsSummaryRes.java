package init.upinmcse.cctvcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryRes {
    private long todayTotal;
    private long yesterdayTotal;
    private long total7d;
    private String topCameraName;
    private long topCameraCount;
}
