package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.response.AnalyticsSummaryRes;
import init.upinmcse.cctvcore.dto.response.CameraRankRes;
import init.upinmcse.cctvcore.dto.response.DailyCountRes;
import init.upinmcse.cctvcore.dto.response.HourlyCountRes;
import init.upinmcse.cctvcore.repository.NotificationRepository;
import init.upinmcse.cctvcore.service.IAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements IAnalyticsService {

    private final NotificationRepository notificationRepository;

    private Instant startOfDayUtc(int daysAgo) {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .minusDays(daysAgo)
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();
    }

    @Override
    public AnalyticsSummaryRes getSummary() {
        Instant todayStart     = startOfDayUtc(0);
        Instant tomorrowStart  = startOfDayUtc(-1);
        Instant yesterdayStart = startOfDayUtc(1);
        Instant sevenDaysAgo   = startOfDayUtc(6);

        long todayTotal     = notificationRepository.countByDetectedAtBetween(todayStart, tomorrowStart);
        long yesterdayTotal = notificationRepository.countByDetectedAtBetween(yesterdayStart, todayStart);
        long total7d        = notificationRepository.countByDetectedAtBetween(sevenDaysAgo, tomorrowStart);

        List<Object[]> topRaw = notificationRepository.getTopCamerasBetween(todayStart, tomorrowStart, 1);
        String topCameraName  = topRaw.isEmpty() ? null : (String) topRaw.get(0)[1];
        long   topCameraCount = topRaw.isEmpty() ? 0 : ((Number) topRaw.get(0)[2]).longValue();

        return AnalyticsSummaryRes.builder()
                .todayTotal(todayTotal)
                .yesterdayTotal(yesterdayTotal)
                .total7d(total7d)
                .topCameraName(topCameraName)
                .topCameraCount(topCameraCount)
                .build();
    }

    @Override
    public List<HourlyCountRes> getHourlyToday() {
        Instant todayStart    = startOfDayUtc(0);
        Instant tomorrowStart = startOfDayUtc(-1);

        List<Object[]> rows = notificationRepository.getHourlyBetween(todayStart, tomorrowStart);
        Map<Integer, Long> counts = rows.stream().collect(Collectors.toMap(
                r -> ((Number) r[0]).intValue(),
                r -> ((Number) r[1]).longValue()
        ));

        return IntStream.range(0, 24)
                .mapToObj(h -> new HourlyCountRes(h, counts.getOrDefault(h, 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyCountRes> getDailyTrend(int days) {
        Instant from          = startOfDayUtc(days - 1);
        Instant tomorrowStart = startOfDayUtc(-1);

        List<Object[]> rows = notificationRepository.getDailyBetween(from, tomorrowStart);
        Map<String, Long> counts = rows.stream().collect(Collectors.toMap(
                r -> (String) r[0],
                r -> ((Number) r[1]).longValue()
        ));

        List<DailyCountRes> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = startOfDayUtc(i).atZone(ZoneOffset.UTC).toLocalDate().toString();
            trend.add(new DailyCountRes(dateStr, counts.getOrDefault(dateStr, 0L)));
        }
        return trend;
    }

    @Override
    public List<CameraRankRes> getTopCameras(int limit) {
        Instant todayStart    = startOfDayUtc(0);
        Instant tomorrowStart = startOfDayUtc(-1);

        return notificationRepository.getTopCamerasBetween(todayStart, tomorrowStart, limit)
                .stream()
                .map(r -> new CameraRankRes(
                        (String) r[0],
                        (String) r[1],
                        ((Number) r[2]).longValue()))
                .collect(Collectors.toList());
    }
}
