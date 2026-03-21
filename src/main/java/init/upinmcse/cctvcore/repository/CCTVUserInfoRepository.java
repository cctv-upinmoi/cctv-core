package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.CCTVUserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CCTVUserInfoRepository extends MongoRepository<CCTVUserInfo, String> {
    Optional<CCTVUserInfo> findByUserId(String userId);
}
