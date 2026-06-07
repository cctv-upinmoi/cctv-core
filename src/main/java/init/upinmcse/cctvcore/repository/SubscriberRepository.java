package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    Optional<Subscriber> findByUserId(String userId);

    List<Subscriber> findAllByEnabledTrue();

    void deleteByUserId(String userId);
}
