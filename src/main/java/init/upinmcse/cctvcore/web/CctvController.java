package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.security.AdminAccess;
import init.upinmcse.cctvcore.security.ConfiguratorAccess;
import init.upinmcse.cctvcore.dto.event.CCTVStatusEvent;
import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.dto.response.ImportCCTVResult;
import init.upinmcse.cctvcore.service.ICCTVService;
import init.upinmcse.cctvcore.service.impl.CCTVSSEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Camera Management", description = "APIs for managing CCTV cameras")
@RestController
@RequestMapping("/cameras")
@RequiredArgsConstructor
public class CctvController {
    private final ICCTVService cctvService;
    private final CCTVSSEService cctvsseService;

    @Operation(summary = "Get all cameras", description = "Return list of all cameras. Publicly accessible, used by AI service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cameras returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public AppResponse<List<CCTVRes>> getAllCameras() {
        return AppResponse.<List<CCTVRes>>builder()
                .data(cctvService.getAllCameras())
                .build();
    }

    @Operation(summary = "Get camera by ID", description = "Return detailed information of a specific CCTV camera by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Camera information returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "404", description = "Camera not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public AppResponse<CCTVRes> getCCTVInfo(
            @Parameter(description = "Camera ID", required = true) @PathVariable String id) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.getCCTVCameraInfoById(id))
                .build();
    }

    @Operation(summary = "Add new camera", description = "Register a new CCTV camera into the system. Requires Configurator role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Camera added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @ConfiguratorAccess
    @PostMapping
    public AppResponse<CCTVRes> addCCTV(@Valid @RequestBody AddCCTVReq cameraInfo) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.addCCTVCameraInfo(cameraInfo))
                .build();
    }

    @Operation(summary = "Import cameras from Excel/CSV", description = "Bulk import CCTV cameras from an Excel or CSV file. Requires Configurator role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import completed, returns success/failure counts and error details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file format or content", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @ConfiguratorAccess
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppResponse<ImportCCTVResult> importFromExcel(
            @Parameter(description = "Excel or CSV file containing camera data", required = true)
            @RequestParam("file") MultipartFile file) {
        return AppResponse.<ImportCCTVResult>builder()
                .data(cctvService.addCCTVfromCSV(file))
                .build();
    }

    @Operation(summary = "Delete camera", description = "Delete a CCTV camera by its ID. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Camera deleted successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Camera not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @AdminAccess
    @DeleteMapping("/{id}")
    public AppResponse<Void> deleteCCTV(
            @Parameter(description = "Camera ID to delete", required = true) @PathVariable String id) {
        cctvService.deleteCCTVCameraInfoById(id);
        return AppResponse.<Void>builder().build();
    }

    @Operation(summary = "Update camera", description = "Update CCTV camera information including connection settings, location, and zones. Requires Configurator role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Camera updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Camera not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @ConfiguratorAccess
    @PatchMapping
    public AppResponse<CCTVRes> updateCCTV(@Valid @RequestBody UpdateCCTVReq updateCameraInfo) {
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.updateCCTVCameraInfo(updateCameraInfo))
                .build();
    }

    @Operation(summary = "Update camera zone", description = "Update the detection zones of a specific CCTV camera. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Camera zone updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Camera not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @AdminAccess
    @PatchMapping("/update-zone")
    public AppResponse<CCTVRes> updateCCTVZone(@Valid @RequestBody UpdateCCTVZoneReq updateCCTVZoneReq){
        return AppResponse.<CCTVRes>builder()
                .data(cctvService.updateCCTVZone(updateCCTVZoneReq))
                .build();
    }

    @Operation(summary = "Stream camera status (SSE)", description = "Subscribe to a Server-Sent Events stream that pushes real-time camera status updates. On connect, a snapshot of all current statuses is sent immediately.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE stream established; events of type 'camera-status' are pushed continuously",
                    content = @Content(mediaType = "text/event-stream")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
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
