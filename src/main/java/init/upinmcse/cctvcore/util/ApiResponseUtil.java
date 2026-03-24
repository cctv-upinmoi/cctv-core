package init.upinmcse.cctvcore.util;

import init.upinmcse.cctvcore.common.AppResponse;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {
    /**
     * @param data
     * @param <T>
     * @return
     */
    public static <T>ResponseEntity<AppResponse<T>> wrapResponse(T data){
        return ResponseEntity.ok().body(AppResponse.success(data));
    }

    /**
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<AppResponse<T>> wrapResponseCustomMessage(T data, String message) {
        return ResponseEntity.ok().body(AppResponse.success(message, data));
    }

    /**
     * @return
     */
    public static ResponseEntity<AppResponse<Void>> wrapNoContent(String message) {
        return ResponseEntity.ok().body(AppResponse.success(message));
    }
}
