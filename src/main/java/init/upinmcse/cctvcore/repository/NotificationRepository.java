package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}