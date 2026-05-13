package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CCTVCameraInfoRepository extends MongoRepository<CCTVCameraInfo, String> {
    Optional<CCTVCameraInfo> findByIndexId(Long s);
    boolean existsByName(String name);
}
