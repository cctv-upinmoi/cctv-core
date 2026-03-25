package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.request.AddCameraRequest;
import init.upinmcse.cctvcore.dto.request.UpdateCameraRequest;
import init.upinmcse.cctvcore.dto.response.CameraResponse;
import init.upinmcse.cctvcore.dto.response.LocationDetailResponse;
import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import init.upinmcse.cctvcore.model.LocationDetail;
import init.upinmcse.cctvcore.repository.CCTVCameraInfoRepository;
import init.upinmcse.cctvcore.service.ICameraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CameraService implements ICameraService {
    private final CCTVCameraInfoRepository cameraInfoRepository;

    @Override
    public CameraResponse addCCTVCameraInfo(AddCameraRequest cameraInfo) {
        return null;
    }

    @Override
    public CameraResponse updateCCTVCameraInfo(UpdateCameraRequest cameraInfo) {
        return null;
    }

    @Override
    public CameraResponse getCCTVCameraInfoById(String id) {
        return null;
    }

    @Override
    public void deleteCCTVCameraInfoById(String id) {

    }

    @Override
    public List<CameraResponse> getAllCameras() {
        return cameraInfoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CameraResponse toResponse(CCTVCameraInfo camera) {
        Double longitude = null;
        Double latitude = null;
        if (camera.getLocation() != null) {
            longitude = camera.getLocation().getX();
            latitude = camera.getLocation().getY();
        }

        LocationDetailResponse locationDetailResponse = null;
        LocationDetail detail = camera.getLocationDetail();
        if (detail != null) {
            locationDetailResponse = LocationDetailResponse.builder()
                    .address(detail.getAddress())
                    .ward(detail.getWard())
                    .district(detail.getDistrict())
                    .province(detail.getProvince())
                    .description(detail.getDescription())
                    .build();
        }

        List<init.upinmcse.cctvcore.dto.response.ZoneResponse> zoneResponses = null;
        if (camera.getZones() != null) {
            zoneResponses = camera.getZones().stream()
                    .map(z -> init.upinmcse.cctvcore.dto.response.ZoneResponse.builder()
                            .name(z.getName())
                            .type(z.getType() != null ? z.getType().name() : null)
                            .enabled(z.isEnabled())
                            .points(z.getPoints())
                            .build())
                    .collect(Collectors.toList());
        }

        return CameraResponse.builder()
                .id(camera.getId())
                .indexId(camera.getIndexId())
                .name(camera.getName())
                .ip(camera.getIp())
                .port(camera.getPort())
                .username(camera.getUsername())
                .status(camera.getStatus() != null ? camera.getStatus().name() : null)
                .mode(camera.getMode() != null ? camera.getMode().name() : null)
                .rtspStreamUrl(camera.getRtspStreamUrl())
                .longitude(longitude)
                .latitude(latitude)
                .locationDetail(locationDetailResponse)
                .zones(zoneResponses)
                .createdAt(camera.getCreatedAt())
                .updatedAt(camera.getUpdatedAt())
                .build();
    }
}

