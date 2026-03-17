package init.upinmcse.cctvcore.exception;

import init.upinmcse.cctvcore.service.I18nMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Environment environment;
    private final I18nMessageService i18nMessageService;

    public GlobalExceptionHandler(Environment environment, I18nMessageService i18nMessageService) {
        this.environment = environment;
        this.i18nMessageService = i18nMessageService;
    }


}
