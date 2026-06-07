package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.request.SubscriberRequest;
import init.upinmcse.cctvcore.model.Subscriber;
import init.upinmcse.cctvcore.security.AdminAccess;
import init.upinmcse.cctvcore.security.ConfiguratorAccess;
import init.upinmcse.cctvcore.service.ISubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConfiguratorAccess
@RestController
@RequestMapping("/notifications/preferences")
@RequiredArgsConstructor
public class SubscriberController {

    private final ISubscriberService subscriberService;

    // ── Own subscriber ────────────────────────────────────────────

    @GetMapping
    public AppResponse<Subscriber> get(@AuthenticationPrincipal Jwt jwt) {
        return AppResponse.success(subscriberService.get(jwt.getSubject()).orElse(null));
    }

    @PutMapping
    public AppResponse<Subscriber> upsert(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SubscriberRequest request) {
        return AppResponse.success(subscriberService.upsert(jwt.getSubject(), request));
    }

    @DeleteMapping
    public AppResponse<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        subscriberService.delete(jwt.getSubject());
        return AppResponse.success("Subscriber deleted");
    }

    @PatchMapping("/toggle")
    public AppResponse<Subscriber> toggle(@AuthenticationPrincipal Jwt jwt) {
        return AppResponse.success(subscriberService.toggleEnabled(jwt.getSubject()));
    }

    // ── Admin operations ──────────────────────────────────────────

    @AdminAccess
    @GetMapping("/admin/all")
    public AppResponse<List<Subscriber>> getAll() {
        return AppResponse.success(subscriberService.getAll());
    }

    @AdminAccess
    @PutMapping("/admin/{userId}")
    public AppResponse<Subscriber> adminUpsert(
            @PathVariable String userId,
            @RequestBody SubscriberRequest request) {
        return AppResponse.success(subscriberService.upsert(userId, request));
    }

    @AdminAccess
    @DeleteMapping("/admin/{userId}")
    public AppResponse<Void> adminDelete(@PathVariable String userId) {
        subscriberService.delete(userId);
        return AppResponse.success("Subscriber deleted");
    }
}
