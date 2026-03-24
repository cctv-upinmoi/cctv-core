package init.upinmcse.cctvcore.mapper;

import init.upinmcse.cctvcore.dto.request.RegistrationRequest;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoResponse;
import init.upinmcse.cctvcore.model.CCTVUserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CCTVUserInfoMapper {
    CCTVUserInfo toCCTVUserInfo(RegistrationRequest request);
    CCTVUserInfoResponse toCCTVUserInfoResponse(CCTVUserInfo userInfo);
}
