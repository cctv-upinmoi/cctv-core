package init.upinmcse.cctvcore.exception;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.service.I18nMessageService;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final I18nMessageService i18nMessageService;

    public GlobalExceptionHandler(I18nMessageService i18nMessageService) {
        this.i18nMessageService = i18nMessageService;
    }

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<AppResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        Locale locale = LocaleContextHolder.getLocale();

        AppResponse appResponse = new AppResponse();
        appResponse.setCode(errorCode.getCode());
        appResponse.setMessage(i18nMessageService.resolveMessage(errorCode.getMessageKey(), locale));

        return ResponseEntity.badRequest().body(appResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<AppResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();

        AppResponse appResponse = new AppResponse();
        appResponse.setCode(errorCode.getCode());
        appResponse.setMessage(i18nMessageService.resolveMessage(errorCode.getMessageKey(), locale));

        return ResponseEntity.status(errorCode.getStatusCode()).body(appResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<AppResponse> handlingAccessDeniedException(AuthorizationDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        Locale locale = LocaleContextHolder.getLocale();

        AppResponse appResponse = new AppResponse();
        appResponse.setCode(errorCode.getCode());
        appResponse.setMessage(i18nMessageService.resolveMessage(errorCode.getMessageKey(), locale));

        return ResponseEntity.status(errorCode.getStatusCode()).body(appResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<AppResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        Locale locale = LocaleContextHolder.getLocale();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = exception.getBindingResult().getAllErrors().getFirst()
                    .unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {
            // Ignore — fallback to INVALID_KEY
        }

        AppResponse appResponse = new AppResponse();
        appResponse.setCode(errorCode.getCode());

        String message = i18nMessageService.resolveMessage(errorCode.getMessageKey(), locale);
        appResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(message, attributes)
                        : message);

        return ResponseEntity.badRequest().body(appResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
