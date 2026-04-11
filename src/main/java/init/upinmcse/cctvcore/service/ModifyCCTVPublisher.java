package init.upinmcse.cctvcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModifyCCTVPublisher {
    private final RedisTemplate<String, String> redisTemplate;

    @Qualifier("modifyCCTVTopic")
    private final ChannelTopic modifyCCTVChannel;

    public void publish(String message) {
        redisTemplate.convertAndSend(modifyCCTVChannel.getTopic(), message);
    }
}
