package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.response.NotificationRes;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.model.Notification;
import init.upinmcse.cctvcore.repository.NotificationRepository;
import init.upinmcse.cctvcore.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Page<NotificationRes> getNotifications(int page, int size, Boolean read) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created_at"));

        Page<Notification> notifications = (read == null)
                ? notificationRepository.findAll(pageable)
                : notificationRepository.findByRead(read, pageable);

        return notifications.map(this::toRes);
    }

    @Override
    public long countUnread() {
        return notificationRepository.countByRead(false);
    }

    @Override
    public void markRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllRead() {
        notificationRepository.findByRead(false, Pageable.unpaged())
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    private NotificationRes toRes(Notification n) {
        return NotificationRes.builder()
                .id(n.getId())
                .eventId(n.getEventId())
                .cameraId(n.getCameraId())
                .cameraName(n.getCameraName())
                .zoneName(n.getZoneName())
                .detectedAt(n.getDetectedAt())
                .imageUrl(n.getImageUrl())
                .read(n.isRead())
                .alertType(n.getAlertType())
                .personCount(n.getPersonCount())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
