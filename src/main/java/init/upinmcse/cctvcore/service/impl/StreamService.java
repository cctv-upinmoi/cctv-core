package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.exception.Go2rtcException;
import init.upinmcse.cctvcore.service.IStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService implements IStreamService {
    private static final String STREAMS_ENDPOINT = "/api/streams";
    private static final String CONFIG_ENDPOINT = "/api/config";
    private static final String RESTART_ENDPOINT = "/api/restart";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP_DEFLATE_BR = "gzip, deflate, br";

    @Override
    public void addOrUpdateCCTV(String deviceName, String rtspSource) throws Go2rtcException {

    }

    @Override
    public void deleteCCTV(String deviceName) {

    }

    @Override
    public boolean syncCCTV(String payload) {
        return false;
    }

    @Override
    public Boolean getStreamHealthCheck(String deviceName) {
        return null;
    }
}
