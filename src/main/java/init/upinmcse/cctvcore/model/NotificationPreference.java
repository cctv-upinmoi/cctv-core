package init.upinmcse.cctvcore.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "CCTVNotificationPreference")
public class NotificationPreference extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("user_id")
    private String userId;

    @Field("email")
    private String email;

    @Field("telegram_chat_id")
    private String telegramChatId;

    @Builder.Default
    @Field("enabled")
    private boolean enabled = true;

    @Builder.Default
    @Field("subscriptions")
    private List<Subscription> subscriptions = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Subscription {

        @Builder.Default
        @Field("subscription_id")
        private String subscriptionId = UUID.randomUUID().toString();

        @Field("camera_id")
        private String cameraId;

        @Field("camera_name")
        private String cameraName;

        /** empty list = tất cả zones của camera đó */
        @Builder.Default
        @Field("zone_names")
        private List<String> zoneNames = new ArrayList<>();

        /** empty list = tất cả loại cảnh báo */
        @Builder.Default
        @Field("alert_types")
        private List<String> alertTypes = new ArrayList<>();

        @Builder.Default
        @Field("channels")
        private List<String> channels = new ArrayList<>();

        @Builder.Default
        @Field("enabled")
        private boolean enabled = true;
    }
}
