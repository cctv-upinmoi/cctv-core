package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.config.ConfiguratorAccess;
import init.upinmcse.cctvcore.dto.request.RegistrationReq;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoRes;
import init.upinmcse.cctvcore.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "APIs for managing CCTV system users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInfoController {
    IUserService userService;

    @Operation(summary = "Register new user", description = "Create a new user account in the system and the identity provider (Keycloak). Requires Configurator role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body, validation failed, or email already in use", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @ConfiguratorAccess
    @PostMapping("/register")
    AppResponse<CCTVUserInfoRes> register(@RequestBody @Valid RegistrationReq request) {
        return AppResponse.<CCTVUserInfoRes>builder()
                .data(userService.register(request))
                .build();
    }

    @Operation(summary = "Get current user profile", description = "Return profile information of the currently authenticated user, extracted from the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    AppResponse<CCTVUserInfoRes> getProfile(){
        return AppResponse.<CCTVUserInfoRes>builder()
                .data(userService.getProfile())
                .build();
    }
}
