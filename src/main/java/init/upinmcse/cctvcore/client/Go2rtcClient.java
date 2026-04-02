package init.upinmcse.cctvcore.client;

import init.upinmcse.cctvcore.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "go2rtc-client", url = "${stream.app.base.url}")
public interface Go2rtcClient {
    // PUT /api/streams?name=...&src=...&src=...
    @PutMapping(Constants.STREAMS_ENDPOINT)
    ResponseEntity<Void> putCamera(
            @RequestParam("name") String name,
            @RequestParam("src") List<String> src
    );

    // DELETE /api/streams?src=...
    @DeleteMapping(Constants.STREAMS_ENDPOINT)
    ResponseEntity<Void> deleteCamera(@RequestParam("src") String src);

    // POST /api/config
    @PostMapping(value = Constants.CONFIG_ENDPOINT, consumes = "text/plain")
    ResponseEntity<Void> syncCameraInventory(@RequestBody String payload);

    // GET /api/frame.jpeg?src=...
    @GetMapping(Constants.SNAP_SHOT_API)
    ResponseEntity<String> getSnapshot(@RequestParam("src") String deviceName);

    // POST /api/restart
    @PostMapping(Constants.RESTART_ENDPOINT)
    ResponseEntity<Void> restartStreamService();
}
