package init.upinmcse.cctvcore.repository;

import init.upinmcse.cctvcore.model.CCTVUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCTVUserInfoRepository extends JpaRepository<CCTVUserInfo, String> {
}
