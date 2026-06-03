package init.upinmcse.cctvcore.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NotificationPreferenceRequest {

    private String userId;
    private String email;
    private String telegramChatId;
    private boolean enabled = true;
    private List<SubscriptionRequest> subscriptions = new ArrayList<>();

    @Data
    public static class SubscriptionRequest {
        private String subscriptionId;
        private String cameraId;
        private String cameraName;
        /** empty = tất cả zones */
        private List<String> zoneNames = new ArrayList<>();
        /** empty = tất cả loại cảnh báo */
        private List<String> alertTypes = new ArrayList<>();
        private List<String> channels = new ArrayList<>();
        private boolean enabled = true;
    }
}
