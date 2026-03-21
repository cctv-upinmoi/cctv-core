package init.upinmcse.cctvcore.service.impl;

import feign.FeignException;
import init.upinmcse.cctvcore.dto.identity.Credential;
import init.upinmcse.cctvcore.dto.identity.TokenExchangeParam;
import init.upinmcse.cctvcore.dto.identity.UserCreationParam;
import init.upinmcse.cctvcore.dto.request.RegistrationRequest;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoResponse;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.exception.ErrorNormalizer;
import init.upinmcse.cctvcore.mapper.CCTVUserInfoMapper;
import init.upinmcse.cctvcore.model.CCTVUserInfo;
import init.upinmcse.cctvcore.repository.CCTVUserInfoRepository;
import init.upinmcse.cctvcore.repository.client.IdentityClient;
import init.upinmcse.cctvcore.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService implements IUserService {
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;
    private final CCTVUserInfoRepository userInfoRepository;
    private final CCTVUserInfoMapper userInfoMapper;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    @Override
    public CCTVUserInfoResponse getProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        CCTVUserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userInfoMapper.toCCTVUserInfoResponse(userInfo);
    }

    @Override
    public CCTVUserInfoResponse register(RegistrationRequest request) {
        try {
            // Create account in KeyCloak
            // Exchange client Token
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);

            // Create user with client Token and given info
            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getEmail())
                            .firstName("")
                            .lastName("")
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
