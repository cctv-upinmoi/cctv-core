package init.upinmcse.cctvcore.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.service.I18nMessageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final I18nMessageService i18nMessageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationEntryPoint(I18nMessageService i18nMessageService) {
        this.i18nMessageService = i18nMessageService;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException
    {
        ErrorCode appErrorCode = ErrorCode.UNAUTHENTICATED;
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
