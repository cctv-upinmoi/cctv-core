package init.upinmcse.cctvcore.mapper;

import init.upinmcse.cctvcore.dto.request.RegistrationReq;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import init.upinmcse.cctvcore.model.CCTVUserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CCTVUserInfoMapper {
    CCTVUserInfo toCCTVUserInfo(RegistrationReq request);
    CCTVUserInfoRes toCCTVUserInfoResponse(CCTVUserInfo userInfo);
}
