package init.upinmcse.cctvcore.repository.client;

import init.upinmcse.cctvcore.dto.identity.TokenExchangeParam;
import init.upinmcse.cctvcore.dto.identity.TokenExchangeResponse;
import init.upinmcse.cctvcore.dto.identity.UserCreationParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import feign.QueryMap;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    /*
    * Exchange token from IDP
    * */
    @PostMapping(
            value = "/realms/${idp.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    /*
    * Create User
    * */
    @PostMapping(value = "/admin/realms/${idp.realm}/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);
}
