package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.config.AdminAccess;
import init.upinmcse.cctvcore.config.ConfiguratorAccess;
import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.service.ICCTVService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cameras")
@RequiredArgsConstructor
public class CctvController {
    private final ICCTVService cctvService;

    @Operation(summary = "Get all cameras", description = "Return list of all cameras for AI service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PermitAll
    @GetMapping
    public AppResponse<List<CCTVRes>> getAllCameras() {
        return AppResponse.<List<CCTVRes>>builder()
                .data(cctvService.getAllCameras())
                .build();
    }

    @GetMapping("/{id}")
    public AppResponse<CCTVRes> getCCTVInfo(@PathVariable String id) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.getCCTVCameraInfoById(id))
                .build();
    }

    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = ""),
            @ApiResponse(responseCode = "403", description = ""),
            @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "")
    })
    @ConfiguratorAccess
    @PostMapping
    public AppResponse<CCTVRes> addCCTV(@Valid @RequestBody AddCCTVReq cameraInfo) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.addCCTVCameraInfo(cameraInfo))
                .build();
    }

    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = ""),
            @ApiResponse(responseCode = "403", description = ""),
            @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "")
    })
    @AdminAccess
    @DeleteMapping("/{id}")
    public AppResponse<String> deleteCCTV(){
        return AppResponse.<String>builder()
                .data("delete")
                .build();
    }

    @ConfiguratorAccess
    @PatchMapping
    public AppResponse<CCTVRes> updateCCTV(@Valid @RequestBody UpdateCCTVReq updateCameraInfo) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.updateCCTVCameraInfo(updateCameraInfo))
                .build();
    }

    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = ""),
            @ApiResponse(responseCode = "403", description = ""),
            @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "")
    })
    @AdminAccess
    @PatchMapping("/update-zone")
    public AppResponse<CCTVRes> updateCCTVZone(@Valid @RequestBody UpdateCCTVZoneReq updateCCTVZoneReq){
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.updateCCTVZone(updateCCTVZoneReq))
                .build();
    }
}
