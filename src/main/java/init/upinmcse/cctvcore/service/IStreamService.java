package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.exception.Go2rtcException;

public interface IStreamService {
    void addOrUpdateCCTV(String deviceName, String rtspSource) throws Go2rtcException;
    void deleteCCTV(String deviceName);
    boolean syncCCTV(String payload);
    Boolean getStreamHealthCheck(String deviceName);
}
