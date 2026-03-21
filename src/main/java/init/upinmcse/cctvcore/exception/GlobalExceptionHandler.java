package init.upinmcse.cctvcore.exception;

import init.upinmcse.cctvcore.common.AppResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
//    private final Environment environment;
//    private final I18nMessageService i18nMessageService;
//
//    public GlobalExceptionHandler(Environment environment, I18nMessageService i18nMessageService) {
//        this.environment = environment;
//        this.i18nMessageService = i18nMessageService;
//    }
    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<AppResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        AppResponse appResponse = new AppResponse();

        appResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        appResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(appResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<AppResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        AppResponse appResponse = new AppResponse();

        appResponse.setCode(errorCode.getCode());
        appResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(appResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<AppResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {

        }

        AppResponse appResponse = new AppResponse();

        appResponse.setCode(errorCode.getCode());
        appResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(appResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

}
