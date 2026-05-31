package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.event.ZoneUpdateEvent;
import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.dto.response.ImportCCTVResult;
import init.upinmcse.cctvcore.event.producer.IModifyCCTVPublisher;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.mapper.CCTVInfoMapper;
import init.upinmcse.cctvcore.mapper.CSVMapper;
import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import init.upinmcse.cctvcore.model.enums.CCTVStatus;
import init.upinmcse.cctvcore.model.Zone;
import init.upinmcse.cctvcore.repository.CCTVCameraInfoRepository;
import init.upinmcse.cctvcore.service.ICCTVService;
import init.upinmcse.cctvcore.service.IStreamService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CCTVService implements ICCTVService {
    private final CCTVCameraInfoRepository cameraInfoRepository;
    private final CCTVInfoMapper CCTVInfoMapper;
    private final PasswordEncoder passwordEncoder;
    private final CSVMapper csvMapper;
    private final IStreamService streamService;
    private final Validator validator;
    private final IModifyCCTVPublisher modifyCCTVPublisher;

    @Override
    @Transactional
    public CCTVRes updateCCTVZone(UpdateCCTVZoneReq updateCCTVZoneReq) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(updateCCTVZoneReq.getCameraId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_FOUND));

        List<Zone> zones = updateCCTVZoneReq.getZones() == null
                ? new ArrayList<>()
                : updateCCTVZoneReq.getZones().stream()
                        .map(z -> Zone.builder()
                                .name(z.getName())
                                .type(z.getType())
                                .enabled(z.isEnabled())
                                .points(z.getPoints())
                                .build())
                        .collect(Collectors.toList());

        camera.setZones(zones);
        CCTVCameraInfo saved = cameraInfoRepository.save(camera);

        // publish event
        ZoneUpdateEvent event = ZoneUpdateEvent.builder()
                .cameraId(camera.getId())
                .zones(camera.getZones())
                .build();
        modifyCCTVPublisher.publish(event);

        return CCTVInfoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CCTVRes addCCTVCameraInfo(AddCCTVReq request) {
        CCTVCameraInfo camera = CCTVInfoMapper.toEntity(request);
        camera.setStatus(CCTVStatus.OK);

        streamService.addOrUpdateCCTV(request.getName(), List.of(request.getRtspStreamUrl()));

        CCTVCameraInfo saved = cameraInfoRepository.save(camera);
        return CCTVInfoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ImportCCTVResult addCCTVfromCSV(MultipartFile csv) {
        List<CCTVRes> imported = new ArrayList<>();
        List<ImportCCTVResult.RowError> errors = new ArrayList<>();
        int rowIndex = 1; // row 1 = header, data bắt đầu từ row 2

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine(); // bỏ qua header
            if (headerLine == null) {
                throw new AppException(ErrorCode.INVALID_FILE); // thêm error code này
            }

            String line;
            while ((line = reader.readLine()) != null) {
                rowIndex++;
                String[] cols = line.split(",", -1); // -1 giữ empty string

                try {
                    AddCCTVReq req = csvMapper.mapToReq(cols, rowIndex, errors);
                    if (req == null) continue; // có lỗi parsing, đã add vào errors

                    // Validate bằng Bean Validation thủ công
                    List<ImportCCTVResult.RowError> validationErrors = validate(req, rowIndex);
                    if (!validationErrors.isEmpty()) {
                        errors.addAll(validationErrors);
                        continue;
                    }

                    if (cameraInfoRepository.existsByName(req.getName())) {
                        errors.add(ImportCCTVResult.RowError.builder()
                                .row(rowIndex)
                                .field("name")
                                .message("Camera name already exists: " + req.getName())
                                .build());
                        continue;
                    }

                    CCTVRes res = addCCTVCameraInfo(req);
                    imported.add(res);

                } catch (Exception e) {
                    log.error("Error importing row {}: {}", rowIndex, e.getMessage());
                    errors.add(ImportCCTVResult.RowError.builder()
                            .row(rowIndex)
                            .field("unknown")
                            .message(e.getMessage())
                            .build());
                }
            }

        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        return ImportCCTVResult.builder()
                .totalRows(rowIndex - 1)
                .successCount(imported.size())
                .failCount(errors.size())
                .imported(imported)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public CCTVRes updateCCTVCameraInfo(UpdateCCTVReq request) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(request.getCameraId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_FOUND));

        CCTVInfoMapper.updateEntity(camera, request);

        if (request.getStatus() != null) {
            try {
                camera.setStatus(CCTVStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_CAMERA_STATUS);
            }
        }

        if (request.getPwd() != null && !request.getPwd().isBlank()) {
            camera.setPwd(passwordEncoder.encode(request.getPwd()));
        }

        CCTVCameraInfo updated = cameraInfoRepository.save(camera);
        return CCTVInfoMapper.toResponse(updated);
    }

    @Override
    public CCTVRes getCCTVCameraInfoById(String id) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_FOUND));
        return CCTVInfoMapper.toResponse(camera);
    }

    @Override
    public void deleteCCTVCameraInfoById(String id) {
        if (!cameraInfoRepository.existsById(id)) {
            throw new AppException(ErrorCode.CAMERA_NOT_FOUND);
        }
        cameraInfoRepository.deleteById(id);
    }

    @Override
    public List<CCTVRes> getAllCameras() {
        return cameraInfoRepository.findAll()
                .stream()
                .map(CCTVInfoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCameraStatus(String id, CCTVStatus status) {
        CCTVCameraInfo camera = cameraInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_FOUND));

        if (camera.getStatus() == status) return;

        camera.setStatus(status);
        cameraInfoRepository.save(camera);
    }

    private List<ImportCCTVResult.RowError> validate(Object obj, int row) {
        return validator.validate(obj).stream()
                .map(v -> ImportCCTVResult.RowError.builder()
                        .row(row)
                        .field(v.getPropertyPath().toString())
                        .message(v.getMessage())
                        .build())
                .collect(Collectors.toList());
    }
}


