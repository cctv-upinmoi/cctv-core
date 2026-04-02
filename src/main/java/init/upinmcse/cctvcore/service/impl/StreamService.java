package init.upinmcse.cctvcore.service.impl;

import com.google.common.collect.Lists;
import feign.FeignException;
import init.upinmcse.cctvcore.client.Go2rtcClient;
import init.upinmcse.cctvcore.exception.Go2rtcException;
import init.upinmcse.cctvcore.service.IStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService implements IStreamService {

    private final Go2rtcClient go2rtcClient;

    @Override
    public void addOrUpdateCCTV(String deviceName, List<String> rtspSource) throws Go2rtcException {
        List<String> validUrls = rtspSource.stream()
                .filter(StringUtils::isNotBlank)
                .toList();
        try {
            ResponseEntity<Void> response = go2rtcClient.putCamera(deviceName, validUrls);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("CAMERA_ADD_OR_EDIT_SUCCESS", deviceName);
            } else {
                log.error("CAMERA_ADD_OR_EDIT_FAIL", deviceName);
            }
        } catch (FeignException e) {
            log.error("COULD_NOT_GET_SUCCESS_RESPONSE_FROM_STREAMING_APP", deviceName, e.getMessage());
            throw new Go2rtcException(e);
        }
    }

    @Override
    public void deleteCCTV(String deviceName) {
        try {
            ResponseEntity<Void> response = go2rtcClient.deleteCamera(deviceName);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("DELETE_CAMERA_DETAILS_FROM_STREAMING_APP", deviceName);
            } else {
                log.error("COULD_NOT_GET_SUCCESS_RESPONSE_FROM_STREAMING_APP", deviceName, response.getStatusCode());
            }
        } catch (FeignException e) {
            log.error("COULD_NOT_GET_SUCCESS_RESPONSE_FROM_STREAMING_APP", deviceName, e.getMessage());
            throw new Go2rtcException(e);
        }
    }

    @Override
    public boolean syncCCTV(String payload) {
        try {
            ResponseEntity<Void> response = go2rtcClient.syncCameraInventory(payload);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("SEND_INVENTORY_CAMERA_DETAILS_TO_STREAMING_APP");
                restartStreamService();
                return true;
            } else {
                log.error("FAILED_TO_SEND_INVENTORY_CAMERA_DETAILS_TO_STREAMING_APP");
                return false;
            }
        } catch (FeignException e) {
            log.error("FAILED_TO_SEND_INVENTORY_CAMERA_DETAILS_TO_STREAMING_APP");
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean getStreamHealthCheck(String deviceName) {
        try {
            ResponseEntity<String> response = go2rtcClient.getSnapshot(deviceName);
            boolean healthy = response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && !response.getBody().isEmpty();
            log.debug("Health check for {}: {}", deviceName, response.getStatusCode());
            return healthy;
        } catch (FeignException e) {
            log.error("getStreamHealthCheck failed for camera {}: {}", deviceName, e.getMessage());
            return false;
        }
    }

    private void restartStreamService(){
        try {
            ResponseEntity<Void> response = go2rtcClient.restartStreamService();
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("RESTART_STREAMING_APP_SUCCESS");
            } else {
                log.error("RESTART_STREAMING_APP_ERROR");
                log.error("STATUS_CODE", response.getStatusCode());
            }
        } catch (FeignException e) {
            log.error("RESTART_STREAMING_APP_ERROR");
            log.error(e.getMessage());
        }
    }
}
