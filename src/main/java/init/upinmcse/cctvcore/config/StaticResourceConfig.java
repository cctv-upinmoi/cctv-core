package init.upinmcse.cctvcore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.snapshot.dir:./snapshots}")
    private String snapshotDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absPath = Paths.get(snapshotDir)
                .toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/snapshots/**")
                .addResourceLocations(absPath);
    }
}