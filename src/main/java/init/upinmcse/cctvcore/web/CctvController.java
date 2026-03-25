package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.configuration.AdminAccess;
import init.upinmcse.cctvcore.configuration.ConfiguratorAccess;
import init.upinmcse.cctvcore.dto.request.AddCameraRequest;
import init.upinmcse.cctvcore.dto.request.UpdateCameraRequest;
import init.upinmcse.cctvcore.dto.response.CameraResponse;
import init.upinmcse.cctvcore.service.ICameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cameras")
@RequiredArgsConstructor
public class CctvController {
    private final ICameraService cameraService;

    @Operation(summary = "Get all cameras", description = "Return list of all cameras for AI service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public AppResponse<List<CameraResponse>> getAllCameras() {
        return AppResponse.<List<CameraResponse>>builder()
                .data(cameraService.getAllCameras())
                .build();
    }

    @GetMapping("/{id}")
    public AppResponse<CameraResponse> getCCTVInfo(@PathVariable String id) {
        return AppResponse.<CameraResponse>builder()
                .data(cameraService.getCCTVCameraInfoById(id))
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
    public AppResponse<CameraResponse> addCCTV(@Valid @RequestBody AddCameraRequest cameraInfo) {
        return AppResponse.<CameraResponse>builder()
                .data(cameraService.addCCTVCameraInfo(cameraInfo))
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
    public AppResponse<CameraResponse> updateCCTV(@Valid @RequestBody UpdateCameraRequest updateCameraInfo) {
        return AppResponse.<CameraResponse>builder()
                .data(cameraService.updateCCTVCameraInfo(updateCameraInfo))
                .build();
    }
}
