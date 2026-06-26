package init.upinmcse.cctvcore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "error.uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "error.invalid-key", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "error.invalid-username", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "error.invalid-password", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "error.unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "error.unauthorized", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "error.email-existed", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1009, "error.user-existed", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1010, "error.username-is-missing", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "error.user-not-existed", HttpStatus.BAD_REQUEST),

    CAMERA_NOT_FOUND(2001, "error.camera-not-found", HttpStatus.NOT_FOUND),
    INVALID_CAMERA_STATUS(2002, "error.invalid-camera-status", HttpStatus.BAD_REQUEST),
    ZONE_NOT_FOUND(2003, "error.zone-not-found", HttpStatus.NOT_FOUND),

    INVALID_FILE(3000, "error.csv-failed", HttpStatus.BAD_REQUEST),

    NOTIFICATION_NOT_FOUND(4001, "error.notification-not-found", HttpStatus.NOT_FOUND),

    JOB_NOT_FOUND(5001, "error.job-not-found", HttpStatus.NOT_FOUND),

    OUTBOX_SERIALIZATION_FAILED(6001, "error.outbox-serialization-failed", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    ErrorCode(int code, String messageKey, HttpStatusCode statusCode) {
        this.code = code;
        this.messageKey = messageKey;
        this.statusCode = statusCode;
    }

    private final int code;
    private final HttpStatusCode statusCode;
    private final String messageKey;
}
