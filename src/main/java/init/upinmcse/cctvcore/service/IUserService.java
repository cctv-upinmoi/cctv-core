package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import org.springframework.security.oauth2.jwt.Jwt;

public interface IUserService {
    CCTVUserInfoRes getProfile(Jwt jwt);
}
