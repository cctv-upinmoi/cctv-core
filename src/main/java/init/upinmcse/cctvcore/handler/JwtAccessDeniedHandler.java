package init.upinmcse.cctvcore.handler;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.service.I18nMessageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final I18nMessageService i18nMessageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAccessDeniedHandler(I18nMessageService i18nMessageService) {
        this.i18nMessageService = i18nMessageService;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        ErrorCode appErrorCode = ErrorCode.UNAUTHORIZED;
        String message = i18nMessageService.resolveMessage(appErrorCode.getMessageKey(), request.getLocale());

        response.setStatus(appErrorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        AppResponse appResponse = AppResponse.builder()
                .code(appErrorCode.getCode())
                .message(message)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(appResponse));
        response.flushBuffer();
    }
}
