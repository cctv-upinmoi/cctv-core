package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.dto.response.ImportCCTVResult;
import init.upinmcse.cctvcore.model.enums.CCTVStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICCTVService {
    CCTVRes addCCTVCameraInfo(AddCCTVReq cameraInfo);
    ImportCCTVResult addCCTVfromCSV(MultipartFile csv);
    CCTVRes updateCCTVCameraInfo(UpdateCCTVReq cameraInfo);
    CCTVRes getCCTVCameraInfoById(String id);
    void deleteCCTVCameraInfoById(String id);
    List<CCTVRes> getAllCameras();
    CCTVRes updateCCTVZone(UpdateCCTVZoneReq updateCCTVZoneReq);
    CCTVRes toggleZoneEnabled(String cameraId, String zoneId, boolean enabled);
    void updateCameraStatus(String id, CCTVStatus status);
}
