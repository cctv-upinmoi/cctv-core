package init.upinmcse.cctvcore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.model.OutboxEvent;
import init.upinmcse.cctvcore.model.enums.OutboxStatus;
import init.upinmcse.cctvcore.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Ghi event vào bảng outbox. CỐ Ý không gắn {@code @Transactional} riêng:
 * method này phải chạy trong transaction của caller (vd: {@code CCTVService.updateCCTVZone},
 * {@code IntrusionEventProcessor.process}) để bản ghi outbox được commit ATOMIC cùng
 * business data — đây là điểm mấu chốt của Outbox pattern, loại bỏ dual-write.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Serialize payload sang JSON và lưu vào outbox với trạng thái PENDING.
     * Payload được giữ nguyên văn để {@code OutboxRelay} publish lên Kafka — JSON
     * sinh ra trùng với định dạng Spring Kafka JsonSerializer (cùng ObjectMapper bean),
     * nên consumer không cần thay đổi.
     *
     * @param topic         Kafka topic đích
     * @param aggregateType loại nguồn event (vd "zone", "notification") — để trace
     * @param key           Kafka message key (giữ thứ tự partition), vd cameraId / eventId
     * @param payload       object sự kiện sẽ được serialize
     */
    public void publish(String topic, String aggregateType, String key, Object payload) {
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            // Ném ra để rollback transaction của caller — không commit business data
            // mà thiếu event tương ứng.
            log.error("Failed to serialize outbox payload topic={} key={}: {}", topic, key, e.getMessage());
            throw new AppException(ErrorCode.OUTBOX_SERIALIZATION_FAILED);
        }

        OutboxEvent event = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(key)
                .topic(topic)
                .payload(json)
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .build();

        outboxRepository.save(event);
        log.debug("Outbox enqueued: topic={} key={} aggregateType={}", topic, key, aggregateType);
    }
}
