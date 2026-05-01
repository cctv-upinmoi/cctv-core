package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.event.CCTVStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CCTVSSEService {

    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(()    -> emitters.remove(emitter));
        emitter.onError(e       -> emitters.remove(emitter));

        return emitter;
    }

    public void broadcast(CCTVStatusEvent event) {
        if (emitters.isEmpty()) return;

        List<SseEmitter> dead = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("camera-status")
                                .data(event, MediaType.APPLICATION_JSON)
                );
            } catch (IOException e) {
                log.warn("SSE emitter dead, removing: {}", e.getMessage());
                dead.add(emitter);
            }
        }

        dead.forEach(emitters::remove);
    }

    /** Chống proxy/nginx timeout */
//    @Scheduled(fixedDelay = 30_000)
//    public void heartbeat() {
//        broadcast(new CameraStatusEvent("heartbeat", Collections.emptyList()));
//    }
}
