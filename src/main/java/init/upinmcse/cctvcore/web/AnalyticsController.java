package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.response.AnalyticsSummaryRes;
import init.upinmcse.cctvcore.dto.response.CameraRankRes;
import init.upinmcse.cctvcore.dto.response.DailyCountRes;
import init.upinmcse.cctvcore.dto.response.HourlyCountRes;
import init.upinmcse.cctvcore.service.IAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final IAnalyticsService analyticsService;

    @GetMapping("/summary")
    public AppResponse<AnalyticsSummaryRes> getSummary() {
        return AppResponse.success(analyticsService.getSummary());
    }

    @GetMapping("/hourly")
    public AppResponse<List<HourlyCountRes>> getHourlyToday() {
        return AppResponse.success(analyticsService.getHourlyToday());
    }

    @GetMapping("/daily")
    public AppResponse<List<DailyCountRes>> getDailyTrend(
            @RequestParam(defaultValue = "7") int days) {
        return AppResponse.success(analyticsService.getDailyTrend(days));
    }

    @GetMapping("/top-cameras")
    public AppResponse<List<CameraRankRes>> getTopCameras(
            @RequestParam(defaultValue = "5") int limit) {
        return AppResponse.success(analyticsService.getTopCameras(limit));
    }
}
