package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.RegistrationRequest;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoResponse;

public interface IUserService {
    CCTVUserInfoResponse register(RegistrationRequest request);
    CCTVUserInfoResponse getProfile();
}
