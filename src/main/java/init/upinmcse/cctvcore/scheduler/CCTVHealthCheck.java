package init.upinmcse.cctvcore.scheduler;

import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.service.ICCTVService;
import init.upinmcse.cctvcore.service.IStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CCTVHealthCheck {

    private final IStreamService streamService;
    private final ICCTVService cctvService;

    @Scheduled(fixedDelayString = "${camera.health-check.interval-ms:60000}")
    public void healthCheckAllCameras() {
        log.info("Starting camera health check...");

        List<CCTVRes> cameras = cctvService.getAllCameras();

        if (cameras.isEmpty()) {
            log.warn("No cameras found for health check.");
            return;
        }

        for (CCTVRes camera : cameras) {
            try {
                boolean isHealthy = streamService.getStreamHealthCheck(camera.getId());
                String newStatus = isHealthy ? "OK" : "ERROR";

                if (!newStatus.equals(camera.getStatus())) {
                    log.info("Camera [{}] status changed: {} -> {}", camera.getName(), camera.getStatus(), newStatus);
//                    cctvService.updateCameraStatus(camera.getId(), newStatus);
                }
            } catch (Exception e) {
                log.error("Health check failed for camera [{}]: {}", camera.getName(), e.getMessage());
            }
        }

        log.info("Camera health check completed.");
    }
}
