package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.service.IStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "r2")
public class R2Storage implements IStorageService {

    private final S3Client r2Client;

    @Value("${cloud-storage.r2.bucket-name}")
    private String bucketName;

    @Value("${cloud-storage.r2.public-url}")
    private String publicUrl;

    @Override
    public String upload(String key, byte[] data, String contentType) {
        r2Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .contentLength((long) data.length)
                        .build(),
                RequestBody.fromBytes(data));

        String url = publicUrl + "/" + key;
        log.info("Uploaded to R2: {}", url);
        return url;
    }

    @Override
    public byte[] download(String key) {
        ResponseBytes<GetObjectResponse> response = r2Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes());
        return response.asByteArray();
    }

    @Override
    public void delete(String key) {
        r2Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        log.info("Deleted from R2: {}", key);
    }
}