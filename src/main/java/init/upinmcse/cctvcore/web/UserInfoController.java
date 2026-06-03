package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import init.upinmcse.cctvcore.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "APIs for managing CCTV system users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInfoController {
    IUserService userService;

    @Operation(summary = "Get current user profile", description = "Return profile information of the currently authenticated user, extracted from the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    AppResponse<CCTVUserInfoRes> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return AppResponse.<CCTVUserInfoRes>builder()
                .data(userService.getProfile(jwt))
                .build();
    }
}
