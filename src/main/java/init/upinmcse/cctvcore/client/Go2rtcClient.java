package init.upinmcse.cctvcore.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "go2rtc-client", url = "${stream.app.base.url}")
public interface Go2rtcClient {
}
