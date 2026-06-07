package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent.RecipientInfo;
import init.upinmcse.cctvcore.dto.request.SubscriberRequest;
import init.upinmcse.cctvcore.model.Subscriber;

import java.util.List;
import java.util.Optional;

public interface ISubscriberService {

    Subscriber upsert(String userId, SubscriberRequest request);
    Optional<Subscriber> get(String userId);
    void delete(String userId);
    Subscriber toggleEnabled(String userId);
    List<Subscriber> getAll();

    List<RecipientInfo> resolveRecipients(IntrusionEvent event);
}
