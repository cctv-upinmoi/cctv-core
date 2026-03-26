package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.RegistrationReq;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;

public interface IUserService {
    CCTVUserInfoRes register(RegistrationReq request);
    CCTVUserInfoRes getProfile();
}
