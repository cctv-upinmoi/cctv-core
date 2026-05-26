package init.upinmcse.cctvcore.event.listener;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.event.processor.IntrusionEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.provider", havingValue = "rabbitmq")
public class RabbitMQIntrusionEventSubscriber {

    private final IntrusionEventProcessor processor;

    @RabbitListener(queues = "${rabbitmq.queues.intrusion-events}")
    public void onMessage(IntrusionEvent event) {
        log.debug("Received intrusion event from RabbitMQ: camera={}", event.getCameraName());
        processor.process(event);
    }
}
