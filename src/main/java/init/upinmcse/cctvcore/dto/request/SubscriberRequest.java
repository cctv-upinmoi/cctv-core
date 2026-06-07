package init.upinmcse.cctvcore.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubscriberRequest {
    private String userId;
    private String email;
    private String telegramChatId;
    private List<String> channels = new ArrayList<>();
    private boolean enabled = true;
    private List<String> jobIds = new ArrayList<>();
}
