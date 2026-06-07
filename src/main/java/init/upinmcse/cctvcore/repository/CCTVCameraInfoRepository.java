package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CCTVCameraInfoRepository extends JpaRepository<CCTVCameraInfo, String> {
    boolean existsByName(String name);
}
