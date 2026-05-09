package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.response.NotificationRes;
import init.upinmcse.cctvcore.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    public AppResponse<Page<NotificationRes>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean read) {
        return AppResponse.success(notificationService.getNotifications(page, size, read));
    }

    @GetMapping("/unread-count")
    public AppResponse<Long> getUnreadCount() {
        return AppResponse.success(notificationService.countUnread());
    }

    @PatchMapping("/{id}/read")
    public AppResponse<Void> markRead(@PathVariable String id) {
        notificationService.markRead(id);
        return AppResponse.success("Marked as read");
    }

    @PatchMapping("/read-all")
    public AppResponse<Void> markAllRead() {
        notificationService.markAllRead();
        return AppResponse.success("All notifications marked as read");
    }
}
