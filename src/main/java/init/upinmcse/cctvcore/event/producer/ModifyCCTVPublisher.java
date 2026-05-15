package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ModifyCCTVPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Qualifier("modifyCCTVTopic")
    private final ChannelTopic modifyCCTVChannel;

    public ModifyCCTVPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("modifyCCTVTopic") ChannelTopic modifyCCTVChannel
    ){
        this.redisTemplate = redisTemplate;
        this.modifyCCTVChannel = modifyCCTVChannel;
    }

    public void publish(ZoneUpdateEvent message) {
        log.debug("Pushlish event update cctv");
        redisTemplate.convertAndSend(modifyCCTVChannel.getTopic(), message);
    }
}
