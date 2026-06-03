package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent.RecipientInfo;
import init.upinmcse.cctvcore.dto.request.NotificationPreferenceRequest;
import init.upinmcse.cctvcore.model.NotificationPreference;
import init.upinmcse.cctvcore.model.NotificationPreference.Subscription;
import init.upinmcse.cctvcore.repository.NotificationPreferenceRepository;
import init.upinmcse.cctvcore.service.INotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationPreferenceService implements INotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    public NotificationPreference upsert(String userId, NotificationPreferenceRequest req) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> NotificationPreference.builder().userId(userId).build());

        pref.setEmail(req.getEmail());
        pref.setTelegramChatId(req.getTelegramChatId());
        pref.setEnabled(req.isEnabled());
        pref.setSubscriptions(req.getSubscriptions().stream()
                .filter(s -> StringUtils.hasText(s.getCameraId())
                          && s.getChannels() != null && !s.getChannels().isEmpty())
                .map(s -> Subscription.builder()
                        .subscriptionId(StringUtils.hasText(s.getSubscriptionId())
                                ? s.getSubscriptionId() : UUID.randomUUID().toString())
                        .cameraId(s.getCameraId())
                        .cameraName(s.getCameraName() != null ? s.getCameraName() : "")
                        .zoneNames(s.getZoneNames() != null ? s.getZoneNames() : new ArrayList<>())
                        .alertTypes(s.getAlertTypes() != null ? s.getAlertTypes() : new ArrayList<>())
                        .channels(s.getChannels())
                        .enabled(s.isEnabled())
                        .build())
                .toList());

        return preferenceRepository.save(pref);
    }

    @Override
    public Optional<NotificationPreference> get(String userId) {
        return preferenceRepository.findByUserId(userId);
    }

    @Override
    public void delete(String userId) {
        preferenceRepository.deleteByUserId(userId);
    }

    @Override
    public NotificationPreference toggleEnabled(String userId) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> NotificationPreference.builder().userId(userId).build());
        pref.setEnabled(!pref.isEnabled());
        return preferenceRepository.save(pref);
    }

    @Override
    public List<RecipientInfo> resolveRecipients(IntrusionEvent event) {
        List<NotificationPreference> allPrefs = preferenceRepository.findAllByEnabledTrue();
        Map<String, RecipientInfo> recipientMap = new LinkedHashMap<>();

        for (NotificationPreference pref : allPrefs) {
            Set<String> matchedChannels = new LinkedHashSet<>();

            for (Subscription sub : pref.getSubscriptions()) {
                if (sub.isEnabled() && matches(sub, event)) {
                    matchedChannels.addAll(sub.getChannels());
                }
            }

            if (matchedChannels.isEmpty()) continue;

            // filter out channels with missing contact info
            Set<String> validChannels = new LinkedHashSet<>();
            if (matchedChannels.contains("EMAIL") && StringUtils.hasText(pref.getEmail())) {
                validChannels.add("EMAIL");
            }
            if (matchedChannels.contains("TELEGRAM") && StringUtils.hasText(pref.getTelegramChatId())) {
                validChannels.add("TELEGRAM");
            }

            if (!validChannels.isEmpty()) {
                recipientMap.put(pref.getUserId(), RecipientInfo.builder()
                        .userId(pref.getUserId())
                        .email(validChannels.contains("EMAIL") ? pref.getEmail() : null)
                        .telegramChatId(validChannels.contains("TELEGRAM") ? pref.getTelegramChatId() : null)
                        .channels(new ArrayList<>(validChannels))
                        .build());
            }
        }

        return new ArrayList<>(recipientMap.values());
    }

    @Override
    public List<NotificationPreference> getAll() {
        return preferenceRepository.findAll();
    }

    @Override
    public NotificationPreference toggleSubscription(String userId, String subscriptionId) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preference not found for user: " + userId));
        pref.getSubscriptions().stream()
                .filter(s -> subscriptionId.equals(s.getSubscriptionId()))
                .findFirst()
                .ifPresent(s -> s.setEnabled(!s.isEnabled()));
        return preferenceRepository.save(pref);
    }

    private boolean matches(Subscription sub, IntrusionEvent event) {
        if (!StringUtils.hasText(sub.getCameraId())) return false;
        if (!sub.getCameraId().equals(event.getCameraId())) return false;
        if (!sub.getZoneNames().isEmpty() && !sub.getZoneNames().contains(event.getZoneName())) return false;
        String alertType = StringUtils.hasText(event.getAlertType()) ? event.getAlertType() : "INTRUSION";
        if (!sub.getAlertTypes().isEmpty() && !sub.getAlertTypes().contains(alertType)) return false;
        return true;
    }
}
