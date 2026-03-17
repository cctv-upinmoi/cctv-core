package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.RegistrationRequest;
import init.upinmcse.cctvcore.dto.response.UserInfoResponse;

public interface IUserService {
    UserInfoResponse register(RegistrationRequest request);
}
