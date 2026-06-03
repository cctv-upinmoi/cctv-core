package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbitmq")
public class RabbitMQNotificationDispatchPublisher implements INotificationDispatchPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitMQNotificationDispatchPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-keys.notification-alert}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publish(NotificationDispatchEvent event) {
        log.debug("Publishing dispatch event to RabbitMQ exchange={} routingKey={} recipients={}",
                exchange, routingKey, event.getRecipients().size());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
