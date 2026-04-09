package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.config.AdminAccess;
import init.upinmcse.cctvcore.config.ConfiguratorAccess;
import init.upinmcse.cctvcore.dto.event.CCTVStatusEvent;
import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.dto.response.ImportCCTVResult;
import init.upinmcse.cctvcore.service.ICCTVService;
import init.upinmcse.cctvcore.service.impl.CCTVSSEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/cameras")
@RequiredArgsConstructor
public class CctvController {
    private final ICCTVService cctvService;
    private final CCTVSSEService cctvsseService;

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
    @ConfiguratorAccess
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppResponse<ImportCCTVResult> importFromExcel(
            @RequestParam("file") MultipartFile file) {
        return AppResponse.<ImportCCTVResult>builder()
                .data(cctvService.addCCTVfromCSV(file))
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
    public AppResponse<Void> deleteCCTV(@PathVariable String id) {
        cctvService.deleteCCTVCameraInfoById(id);
        return AppResponse.<Void>builder().build();
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

    @GetMapping(value = "/status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamCameraStatus() {
        SseEmitter emitter = cctvsseService.subscribe();

        // Check snapshot ngay khi vừa connect
        try {
            List<CCTVStatusEvent.CCTVStatus> snapshot = cctvService.getAllCameras()
                    .stream()
                    .map(c -> new CCTVStatusEvent.CCTVStatus(
                            c.getId(), c.getName(), c.getStatus(), Instant.now()
                    ))
                    .toList();

            emitter.send(
                    SseEmitter.event()
                            .name("camera-status")
                            .data(new CCTVStatusEvent("snapshot", snapshot), MediaType.APPLICATION_JSON)
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
