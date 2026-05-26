package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/notifications/by-event")
@RequiredArgsConstructor
@Slf4j
public class ClipController {

    private final INotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String WS_CLIP_READY = "/topic/clip-ready";

    /**
     * AI service gọi endpoint này sau khi encode xong clip (~10s sau alert).
     * Lưu video vào storage, cập nhật notification, push WebSocket để UI hiển thị.
     */
    @PatchMapping("/{eventId}/video")
    public AppResponse<Void> attachVideo(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Received clip for event_id={} size={}B", eventId, file.getSize());
        String videoUrl = notificationService.attachVideo(eventId, file.getBytes());

        messagingTemplate.convertAndSend(WS_CLIP_READY,
                Map.of("eventId", eventId, "videoUrl", videoUrl));
        log.info("Clip attached and broadcast: event_id={} url={}", eventId, videoUrl);

        return AppResponse.success("Video attached");
    }
}
