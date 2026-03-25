package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.AddCameraRequest;
import init.upinmcse.cctvcore.dto.request.UpdateCameraRequest;
import init.upinmcse.cctvcore.dto.response.CameraResponse;

import java.util.List;

public interface ICameraService {
    CameraResponse addCCTVCameraInfo(AddCameraRequest cameraInfo);
    CameraResponse updateCCTVCameraInfo(UpdateCameraRequest cameraInfo);
    CameraResponse getCCTVCameraInfoById(String id);
    void deleteCCTVCameraInfoById(String id);
    List<CameraResponse> getAllCameras();
}
