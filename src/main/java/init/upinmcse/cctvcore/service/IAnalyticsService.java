package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.response.AnalyticsSummaryRes;
import init.upinmcse.cctvcore.dto.response.CameraRankRes;
import init.upinmcse.cctvcore.dto.response.DailyCountRes;
import init.upinmcse.cctvcore.dto.response.HourlyCountRes;

import java.util.List;

public interface IAnalyticsService {

    /** Stat cards: today, yesterday, 7-day totals + top camera today */
    AnalyticsSummaryRes getSummary();

    /** 24 hourly buckets for today (UTC) */
    List<HourlyCountRes> getHourlyToday();

    /** One row per day for the last {@code days} days (UTC dates, oldest first) */
    List<DailyCountRes> getDailyTrend(int days);

    /** Top cameras ranked by alert count today */
    List<CameraRankRes> getTopCameras(int limit);
}
