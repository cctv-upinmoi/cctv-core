package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.request.RegistrationRequest;
import init.upinmcse.cctvcore.dto.response.CCTVUserInfoResponse;
import init.upinmcse.cctvcore.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInfoController {
    IUserService userService;

    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(mediaType = "application/xml")),
            @ApiResponse(responseCode = "400", description = ""),
            @ApiResponse(responseCode = "403", description = ""),
            @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "")
    })
    @PostMapping("/register")
    AppResponse<CCTVUserInfoResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return AppResponse.<CCTVUserInfoResponse>builder()
                .data(userService.register(request))
                .build();
    }
    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(mediaType = "application/xml")),
            @ApiResponse(responseCode = "400", description = ""),
            @ApiResponse(responseCode = "403", description = ""),
            @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "")
    })
    @GetMapping
    AppResponse<CCTVUserInfoResponse> getProfile(){
        return AppResponse.<CCTVUserInfoResponse>builder()
                .data(userService.getProfile())
                .build();
    }
}
