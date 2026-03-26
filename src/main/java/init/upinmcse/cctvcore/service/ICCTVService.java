package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVZoneReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;

import java.util.List;

public interface ICCTVService {
    CCTVRes addCCTVCameraInfo(AddCCTVReq cameraInfo);
    CCTVRes updateCCTVCameraInfo(UpdateCCTVReq cameraInfo);
    CCTVRes getCCTVCameraInfoById(String id);
    void deleteCCTVCameraInfoById(String id);
    List<CCTVRes> getAllCameras();
    CCTVRes updateCCTVZone(UpdateCCTVZoneReq updateCCTVZoneReq);
}
