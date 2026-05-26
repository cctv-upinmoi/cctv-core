package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;

public interface IModifyCCTVPublisher {
    void publish(ZoneUpdateEvent event);
}
