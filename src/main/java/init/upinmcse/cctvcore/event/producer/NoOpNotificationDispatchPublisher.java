package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.provider", havingValue = "redis")
public class NoOpNotificationDispatchPublisher implements INotificationDispatchPublisher {

    @Override
    public void publish(NotificationDispatchEvent event) {
        // no-op: redis provider does not support notification dispatch
    }
}
