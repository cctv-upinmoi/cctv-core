package init.upinmcse.cctvcore.service.impl;

import feign.FeignException;
import init.upinmcse.cctvcore.dto.identity.Credential;
import init.upinmcse.cctvcore.dto.identity.TokenExchangeParam;
import init.upinmcse.cctvcore.dto.identity.UserCreationParam;
import init.upinmcse.cctvcore.dto.request.RegistrationReq;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.exception.ErrorNormalizer;
import init.upinmcse.cctvcore.mapper.CCTVUserInfoMapper;
import init.upinmcse.cctvcore.model.CCTVUserInfo;
import init.upinmcse.cctvcore.repository.CCTVUserInfoRepository;
import init.upinmcse.cctvcore.client.IdpClient;
import init.upinmcse.cctvcore.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService implements IUserService {
    private final IdpClient idpClient;
    private final ErrorNormalizer errorNormalizer;
    private final CCTVUserInfoRepository userInfoRepository;
    private final CCTVUserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    @Override
    public CCTVUserInfoRes getProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info("userId {}",userId);

        CCTVUserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userInfoMapper.toCCTVUserInfoResponse(userInfo);
    }

    @Override
    public CCTVUserInfoRes register(RegistrationReq request) {
        try {
            // Create account in KeyCloak
            // Exchange client Token
            var token = idpClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);

            // Create user with client Token and given info
            // Get userId of keyCloak account
            var creationResponse = idpClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getEmail())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            log.info("UserId {}", userId);

            // save to db
            CCTVUserInfo userInfo = userInfoMapper.toCCTVUserInfo(request);
            userInfo.setUserId(userId);

            userInfo = userInfoRepository.save(userInfo);

            return userInfoMapper.toCCTVUserInfoResponse(userInfo);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = Objects.requireNonNull(response.getHeaders().get("Location")).getFirst();
        String[] splitStr = location.split("/");
        return splitStr[splitStr.length - 1];
    }
}
