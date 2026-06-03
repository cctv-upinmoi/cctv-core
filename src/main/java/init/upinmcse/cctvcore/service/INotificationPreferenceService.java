package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent.RecipientInfo;
import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.request.NotificationPreferenceRequest;
import init.upinmcse.cctvcore.model.NotificationPreference;

import java.util.List;
import java.util.Optional;

public interface INotificationPreferenceService {

    NotificationPreference upsert(String userId, NotificationPreferenceRequest request);

    Optional<NotificationPreference> get(String userId);

    void delete(String userId);

    NotificationPreference toggleEnabled(String userId);

    List<RecipientInfo> resolveRecipients(IntrusionEvent event);

    List<NotificationPreference> getAll();

    NotificationPreference toggleSubscription(String userId, String subscriptionId);
}
