package init.upinmcse.cctvcore.event.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ModifyCCTVPublisher implements CommandLineRunner {
    private final RedisTemplate<String, String> redisTemplate;

    @Qualifier("modifyCCTVTopic")
    private final ChannelTopic modifyCCTVChannel;

    public ModifyCCTVPublisher(
            RedisTemplate<String, String> redisTemplate,
            @Qualifier("modifyCCTVTopic") ChannelTopic modifyCCTVChannel
    ){
        this.redisTemplate = redisTemplate;
        this.modifyCCTVChannel = modifyCCTVChannel;
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(modifyCCTVChannel.getTopic(), message);
    }

    @Override
    public void run(String... args) throws Exception {
        String message = "Hello World from Java Spring Boot!";
        redisTemplate.convertAndSend("hello-channel", message);
        System.out.println("[PUB] Sent: " + message);
    }
}
