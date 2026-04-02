package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.exception.Go2rtcException;

import java.util.List;

public interface IStreamService {
    void addOrUpdateCCTV(String deviceName, List<String> rtspSource) throws Go2rtcException;
    void deleteCCTV(String deviceName);
    boolean syncCCTV(String payload);
    Boolean getStreamHealthCheck(String deviceName);
}
