package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.response.NotificationRes;
import org.springframework.data.domain.Page;

public interface INotificationService {

    Page<NotificationRes> getNotifications(int page, int size, Boolean read);

    long countUnread();

    void markRead(String id);

    void markAllRead();
}
