package init.upinmcse.cctvcore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbitmq")
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.intrusion-events}")
    private String intrusionQueue;

    @Value("${rabbitmq.queues.modify-cctv}")
    private String modifyCCTVQueue;

    @Value("${rabbitmq.queues.notification-dispatch}")
    private String notificationQueue;

    @Value("${rabbitmq.routing-keys.intrusion-events}")
    private String intrusionRoutingKey;

    @Value("${rabbitmq.routing-keys.modify-cctv}")
    private String modifyCCTVRoutingKey;

    @Value("${rabbitmq.routing-keys.notification-alert}")
    private String notificationRoutingKey;

    @Bean
    public TopicExchange cctvExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue intrusionEventQueue() {
        return QueueBuilder.durable(intrusionQueue).build();
    }

    @Bean
    public Queue modifyCCTVQueue() {
        return QueueBuilder.durable(modifyCCTVQueue).build();
    }

    @Bean
    public Binding intrusionBinding(Queue intrusionEventQueue, TopicExchange cctvExchange) {
        return BindingBuilder.bind(intrusionEventQueue).to(cctvExchange).with(intrusionRoutingKey);
    }

    @Bean
    public Binding modifyCCTVBinding(Queue modifyCCTVQueue, TopicExchange cctvExchange) {
        return BindingBuilder.bind(modifyCCTVQueue).to(cctvExchange).with(modifyCCTVRoutingKey);
    }

    @Bean
    public Queue notificationDispatchQueue() {
        return QueueBuilder.durable(notificationQueue).build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationDispatchQueue, TopicExchange cctvExchange) {
        return BindingBuilder.bind(notificationDispatchQueue).to(cctvExchange).with(notificationRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
