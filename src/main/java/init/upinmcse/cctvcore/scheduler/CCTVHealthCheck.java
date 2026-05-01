package init.upinmcse.cctvcore.scheduler;

import init.upinmcse.cctvcore.dto.event.CCTVStatusEvent;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.model.enums.CCTVStatus;
import init.upinmcse.cctvcore.service.ICCTVService;
import init.upinmcse.cctvcore.service.IStreamService;
import init.upinmcse.cctvcore.service.CCTVSSEService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CCTVHealthCheck {

    private final IStreamService   streamService;
    private final ICCTVService     cctvService;
    private final CCTVSSEService   cctvSSEService;

//    @Scheduled(fixedDelayString = "${camera.health-check.interval-ms:60000}")
    public void healthCheckAllCameras() {
        log.info("Starting camera health check...");

        List<CCTVRes> cameras = cctvService.getAllCameras();
        if (cameras.isEmpty()) {
            log.warn("No cameras found for health check.");
            return;
        }

        List<CCTVStatusEvent.CCTVStatus> results = new ArrayList<>();

        for (CCTVRes camera : cameras) {
            CCTVStatus newStatus = CCTVStatus.OK;
            try {
                boolean isHealthy = streamService.getStreamHealthCheck(camera.getId());
                newStatus = isHealthy ? CCTVStatus.OK : CCTVStatus.NOK;

                log.info("Camera [{}] status changed: {} -> {}",
                        camera.getName(), camera.getStatus(), newStatus);
                cctvService.updateCameraStatus(camera.getId(), newStatus);
            } catch (Exception e) {
                log.error("Health check failed for camera [{}]: {}", camera.getName(), e.getMessage());
            }

            results.add(new CCTVStatusEvent.CCTVStatus(
                    camera.getId(),
                    camera.getName(),
                    newStatus.toString(),
                    Instant.now()
            ));
        }
        cctvSSEService.broadcast(new CCTVStatusEvent("update", results));
        log.info("Camera health check completed. Broadcasted {} results.", results.size());
    }
}
