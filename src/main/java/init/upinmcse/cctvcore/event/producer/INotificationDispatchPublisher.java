package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;

public interface INotificationDispatchPublisher {
    void publish(NotificationDispatchEvent event);
}
