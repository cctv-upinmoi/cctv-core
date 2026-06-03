package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import init.upinmcse.cctvcore.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService implements IUserService {

    @Override
    public CCTVUserInfoRes getProfile(Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        List<String> roles = Collections.emptyList();
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> rawRoles) {
            roles = rawRoles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .filter(r -> r.equals(r.toUpperCase()))
                    .toList();
        }

        log.info("getProfile userId={}", userId);

        return CCTVUserInfoRes.builder()
                .userId(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .roles(roles)
                .build();
    }
}
