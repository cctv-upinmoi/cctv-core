package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findAllByEnabledTrue();
    List<Job> findByCameraId(String cameraId);
}
