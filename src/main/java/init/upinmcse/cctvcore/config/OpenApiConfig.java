package init.upinmcse.cctvcore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class OpenApiConfig {
    private static final String JWT_AUTHORIZATION = "JWT Authorization";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart CCTV Core API")
                        .description("REST API for managing CCTV cameras, users, and real-time status streaming in the Smart CCTV system.")
                        .version("1.0.0")
                        .contact(new Contact().name("Smart CCTV Team")))
                .components(componentsConfiguration())
                .security(List.of(bearerJWTToken()));
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .scheme("bearer")
                .name("Authentication")
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER);
    }

    private Components componentsConfiguration() {
        return new Components()
                .addSecuritySchemes(JWT_AUTHORIZATION, securityScheme());
    }

    private SecurityRequirement bearerJWTToken() {
        SecurityRequirement requirement = new SecurityRequirement();
        requirement.addList(JWT_AUTHORIZATION, Collections.emptyList());
        return requirement;
    }

}
