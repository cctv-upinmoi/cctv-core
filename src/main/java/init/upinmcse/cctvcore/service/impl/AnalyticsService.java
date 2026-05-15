package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.response.AnalyticsSummaryRes;
import init.upinmcse.cctvcore.dto.response.CameraRankRes;
import init.upinmcse.cctvcore.dto.response.DailyCountRes;
import init.upinmcse.cctvcore.dto.response.HourlyCountRes;
import init.upinmcse.cctvcore.model.Notification;
import init.upinmcse.cctvcore.service.IAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements IAnalyticsService {

    private final MongoTemplate mongoTemplate;

    // ── time helpers ──────────────────────────────────────────────────────

    private Instant startOfDayUtc(int daysAgo) {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .minusDays(daysAgo)
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();
    }

    // ── Summary ───────────────────────────────────────────────────────────

    @Override
    public AnalyticsSummaryRes getSummary() {
        Instant todayStart     = startOfDayUtc(0);
        Instant tomorrowStart  = startOfDayUtc(-1);   // tomorrow
        Instant yesterdayStart = startOfDayUtc(1);
        Instant sevenDaysAgo   = startOfDayUtc(6);

        long todayTotal = mongoTemplate.count(
                Query.query(Criteria.where("detected_at").gte(todayStart).lt(tomorrowStart)),
                Notification.class);

        long yesterdayTotal = mongoTemplate.count(
                Query.query(Criteria.where("detected_at").gte(yesterdayStart).lt(todayStart)),
                Notification.class);

        long total7d = mongoTemplate.count(
                Query.query(Criteria.where("detected_at").gte(sevenDaysAgo).lt(tomorrowStart)),
                Notification.class);

        // Top camera today
        List<CameraRankRes> topCams = getTopCamerasInRange(todayStart, tomorrowStart, 1);
        String topCameraName = topCams.isEmpty() ? null : topCams.get(0).getCameraName();
        long   topCameraCount = topCams.isEmpty() ? 0 : topCams.get(0).getCount();

        return AnalyticsSummaryRes.builder()
                .todayTotal(todayTotal)
                .yesterdayTotal(yesterdayTotal)
                .total7d(total7d)
                .topCameraName(topCameraName)
                .topCameraCount(topCameraCount)
                .build();
    }

    // ── Hourly today ──────────────────────────────────────────────────────

    @Override
    public List<HourlyCountRes> getHourlyToday() {
        Instant todayStart    = startOfDayUtc(0);
        Instant tomorrowStart = startOfDayUtc(-1);

        MatchOperation match = Aggregation.match(
                Criteria.where("detected_at").gte(todayStart).lt(tomorrowStart));

        ProjectionOperation project = Aggregation.project()
                .and(DateOperators.Hour.hourOf("detected_at")).as("hour");

        GroupOperation group = Aggregation.group("hour").count().as("count");

        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"));

        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(match, project, group, sort),
                Notification.class,
                Document.class);

        // Build sparse map then fill all 24 hours
        Map<Integer, Long> counts = results.getMappedResults().stream()
                .collect(Collectors.toMap(
                        d -> d.getInteger("_id"),
                        d -> ((Number) d.get("count")).longValue()
                ));

        return IntStream.range(0, 24)
                .mapToObj(h -> new HourlyCountRes(h, counts.getOrDefault(h, 0L)))
                .collect(Collectors.toList());
    }

    // ── Daily trend ───────────────────────────────────────────────────────

    @Override
    public List<DailyCountRes> getDailyTrend(int days) {
        Instant from          = startOfDayUtc(days - 1);
        Instant tomorrowStart = startOfDayUtc(-1);

        MatchOperation match = Aggregation.match(
                Criteria.where("detected_at").gte(from).lt(tomorrowStart));

        ProjectionOperation project = Aggregation.project()
                .and(DateOperators.DateToString.dateOf("detected_at")
                        .toString("%Y-%m-%d")
                        .withTimezone(DateOperators.Timezone.valueOf("UTC")))
                .as("date");

        GroupOperation group = Aggregation.group("date").count().as("count");

        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"));

        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(match, project, group, sort),
                Notification.class,
                Document.class);

        // Sparse map from DB
        Map<String, Long> counts = results.getMappedResults().stream()
                .collect(Collectors.toMap(
                        d -> d.getString("_id"),
                        d -> ((Number) d.get("count")).longValue()
                ));

        // Fill every day in the range, oldest first
        List<DailyCountRes> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = startOfDayUtc(i)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                    .toString(); // yyyy-MM-dd
            trend.add(new DailyCountRes(dateStr, counts.getOrDefault(dateStr, 0L)));
        }
        return trend;
    }

    // ── Top cameras ───────────────────────────────────────────────────────

    @Override
    public List<CameraRankRes> getTopCameras(int limit) {
        Instant todayStart    = startOfDayUtc(0);
        Instant tomorrowStart = startOfDayUtc(-1);
        return getTopCamerasInRange(todayStart, tomorrowStart, limit);
    }

    private List<CameraRankRes> getTopCamerasInRange(Instant from, Instant to, int limit) {
        MatchOperation match = Aggregation.match(
                Criteria.where("detected_at").gte(from).lt(to));

        GroupOperation group = Aggregation.group("camera_id")
                .first("camera_name").as("cameraName")
                .count().as("count");

        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "count"));

        LimitOperation limitOp = Aggregation.limit(limit);

        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(match, group, sort, limitOp),
                Notification.class,
                Document.class);

        return results.getMappedResults().stream()
                .map(d -> new CameraRankRes(
                        d.getString("_id"),
                        d.getString("cameraName"),
                        ((Number) d.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }
}
