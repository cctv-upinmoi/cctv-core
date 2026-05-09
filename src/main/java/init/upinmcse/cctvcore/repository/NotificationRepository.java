package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Page<Notification> findByRead(boolean read, Pageable pageable);

    long countByRead(boolean read);
}