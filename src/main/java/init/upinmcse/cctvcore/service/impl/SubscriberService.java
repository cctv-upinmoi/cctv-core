package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.event.IntrusionEvent;
import init.upinmcse.cctvcore.dto.event.NotificationDispatchEvent.RecipientInfo;
import init.upinmcse.cctvcore.dto.request.SubscriberRequest;
import init.upinmcse.cctvcore.model.Job;
import init.upinmcse.cctvcore.model.Subscriber;
import init.upinmcse.cctvcore.repository.JobRepository;
import init.upinmcse.cctvcore.repository.SubscriberRepository;
import init.upinmcse.cctvcore.service.ISubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriberService implements ISubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public Subscriber upsert(String userId, SubscriberRequest req) {
        Subscriber subscriber = subscriberRepository.findByUserId(userId)
                .orElseGet(() -> Subscriber.builder().userId(userId).build());

        subscriber.setEmail(req.getEmail());
        subscriber.setTelegramChatId(req.getTelegramChatId());
        subscriber.setChannels(req.getChannels() != null ? req.getChannels() : new ArrayList<>());
        subscriber.setEnabled(req.isEnabled());

        // Link jobs by ID
        if (req.getJobIds() != null) {
            List<Job> jobs = jobRepository.findAllById(req.getJobIds());
            subscriber.setJobs(jobs);
        }

        return subscriberRepository.save(subscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscriber> get(String userId) {
        return subscriberRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void delete(String userId) {
        subscriberRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public Subscriber toggleEnabled(String userId) {
        Subscriber subscriber = subscriberRepository.findByUserId(userId)
                .orElseGet(() -> Subscriber.builder().userId(userId).build());
        subscriber.setEnabled(!subscriber.isEnabled());
        return subscriberRepository.save(subscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscriber> getAll() {
        return subscriberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipientInfo> resolveRecipients(IntrusionEvent event) {
        String alertType = StringUtils.hasText(event.getAlertType()) ? event.getAlertType() : "INTRUSION";

        List<Subscriber> activeSubscribers = subscriberRepository.findAllByEnabledTrue();
        Map<String, RecipientInfo> recipientMap = new LinkedHashMap<>();

        for (Subscriber subscriber : activeSubscribers) {
            boolean hasMatchingJob = subscriber.getJobs().stream()
                    .filter(Job::isEnabled)
                    .anyMatch(job ->
                            event.getCameraId().equals(job.getCameraId()) &&
                            (job.getAlertTypes().isEmpty() || job.getAlertTypes().contains(alertType))
                    );

            if (!hasMatchingJob) continue;

            Set<String> validChannels = new LinkedHashSet<>();
            if (subscriber.getChannels().contains("EMAIL") && StringUtils.hasText(subscriber.getEmail())) {
                validChannels.add("EMAIL");
            }
            if (subscriber.getChannels().contains("TELEGRAM") && StringUtils.hasText(subscriber.getTelegramChatId())) {
                validChannels.add("TELEGRAM");
            }

            if (!validChannels.isEmpty()) {
                recipientMap.put(subscriber.getUserId(), RecipientInfo.builder()
                        .userId(subscriber.getUserId())
                        .email(validChannels.contains("EMAIL") ? subscriber.getEmail() : null)
                        .telegramChatId(validChannels.contains("TELEGRAM") ? subscriber.getTelegramChatId() : null)
                        .channels(new ArrayList<>(validChannels))
                        .build());
            }
        }

        return new ArrayList<>(recipientMap.values());
    }
}
