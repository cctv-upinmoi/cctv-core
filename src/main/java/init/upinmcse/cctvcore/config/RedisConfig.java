package init.upinmcse.cctvcore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.event.listener.IntrusionEventSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.channels.intrusion-events}")
    private String intrusionEventChannel;

    @Value("${redis.channels.modify-cctv}")
    private String modifyCCTVChannel;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public ChannelTopic intrusionEventTopic() {
        return new ChannelTopic(intrusionEventChannel);
    }

    @Bean
    public ChannelTopic modifyCCTVTopic() {
        return new ChannelTopic(modifyCCTVChannel);
    }

    @Bean
    @ConditionalOnProperty(name = "messaging.provider", havingValue = "redis", matchIfMissing = true)
    public MessageListenerAdapter intrusionListenerAdapter(IntrusionEventSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    @ConditionalOnProperty(name = "messaging.provider", havingValue = "redis", matchIfMissing = true)
    public RedisMessageListenerContainer redisListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter intrusionListenerAdapter,
            ChannelTopic intrusionEventTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(intrusionListenerAdapter, intrusionEventTopic);
        return container;
    }
}