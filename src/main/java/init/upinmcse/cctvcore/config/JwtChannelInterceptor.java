package init.upinmcse.cctvcore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * Validates JWT on STOMP CONNECT frames.
 * The token is read from the native header "Authorization: Bearer <token>".
 * On success, the authenticated principal is attached to the STOMP session
 * via {@link StompHeaderAccessor#setUser(java.security.Principal)}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message; // only validate on CONNECT
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[WS] CONNECT rejected: missing or malformed Authorization header");
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }

        String rawToken = authHeader.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(rawToken);
            Authentication auth = jwtAuthenticationConverter.convert(jwt);
            accessor.setUser(auth);
            log.debug("[WS] CONNECT authenticated: subject={}", jwt.getSubject());
        } catch (JwtException e) {
            log.warn("[WS] CONNECT rejected: {}", e.getMessage());
            throw new AccessDeniedException("Invalid JWT token");
        }

        return message;
    }
}