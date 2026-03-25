package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.request.AddCameraRequest;
import init.upinmcse.cctvcore.dto.request.UpdateCameraRequest;
import init.upinmcse.cctvcore.dto.response.CameraResponse;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.mapper.CameraMapper;
import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import init.upinmcse.cctvcore.model.CCTVStatus;
import init.upinmcse.cctvcore.repository.CCTVCameraInfoRepository;
import init.upinmcse.cctvcore.service.ICameraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CameraService implements ICameraService {
    private final CCTVCameraInfoRepository cameraInfoRepository;
    private final CameraMapper cameraMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CameraResponse addCCTVCameraInfo(AddCameraRequest request) {
        CCTVCameraInfo camera = cameraMapper.toEntity(request);
        camera.setStatus(CCTVStatus.OK);
        
        // Auto-generate indexId if you need it sequence-based, or leave null to handle later.
        
        CCTVCameraInfo saved = cameraInfoRepository.save(camera);
        return cameraMapper.toResponse(saved);
    }

    @Override
    public CameraResponse updateCCTVCameraInfo(UpdateCameraRequest request) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(request.getCameraId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // need add CAMERA_NOT_FOUND exception

        cameraMapper.updateEntity(camera, request);
        
        if (request.getStatus() != null) {
            try {
                camera.setStatus(CCTVStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore or throw error if status is invalid
            }
        }

        camera.setPwd(passwordEncoder.encode(request.getPwd()));
        CCTVCameraInfo updated = cameraInfoRepository.save(camera);
        return cameraMapper.toResponse(updated);
    }

    @Override
    public CameraResponse getCCTVCameraInfoById(String id) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return cameraMapper.toResponse(camera);
    }

    @Override
    public void deleteCCTVCameraInfoById(String id) {
        if (!cameraInfoRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        cameraInfoRepository.deleteById(id);
    }

    @Override
    public List<CameraResponse> getAllCameras() {
        return cameraInfoRepository.findAll()
                .stream()
                .map(cameraMapper::toResponse)
                .collect(Collectors.toList());
    }
}


