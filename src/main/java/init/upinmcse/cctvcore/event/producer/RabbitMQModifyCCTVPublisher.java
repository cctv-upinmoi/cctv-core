package init.upinmcse.cctvcore.event.producer;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbitmq")
public class RabbitMQModifyCCTVPublisher implements IModifyCCTVPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitMQModifyCCTVPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-keys.modify-cctv}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publish(ZoneUpdateEvent event) {
        log.debug("Publishing zone update event to RabbitMQ exchange={} routingKey={}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
