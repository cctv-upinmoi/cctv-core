package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.service.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorage implements IStorageService {

    @Value("${app.snapshot.dir:./snapshots}")
    private String snapshotDir;

    @Value("${app.storage.base-url:http://localhost:8080/cctv-core/snapshots}")
    private String baseUrl;

    @Override
    public String upload(String key, byte[] data, String contentType) {
        try {
            Path file = Paths.get(snapshotDir, key);
            Files.createDirectories(file.getParent());
            Files.write(file, data);
            String url = baseUrl + "/" + key;
            log.info("Saved snapshot locally: {}", file);
            return url;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save snapshot: " + key, e);
        }
    }

    @Override
    public byte[] download(String key) {
        try {
            return Files.readAllBytes(Paths.get(snapshotDir, key));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read snapshot: " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(Paths.get(snapshotDir, key));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete snapshot: " + key, e);
        }
    }
}