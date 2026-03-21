package init.upinmcse.cctvcore.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException
    {
        ErrorCode appErrorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(appErrorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(AppResponse.error(appErrorCode)));
        response.flushBuffer();
    }
}
