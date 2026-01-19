package se.ifmo.origin_backend.storage;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageService {
    private static final String STAGING_PREFIX = "staging";
    private static final String IMPORTS_PREFIX = "imports";
    private static final long PART_SIZE = 10 * 1024 * 1024;

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public PreparedImportFile prepareImportFile(
        long importId,
        String originalFileName,
        InputStream inputStream,
        long size,
        String contentType) throws Exception {
        ensureBucket();

        String safeName = FileNameSanitizer.sanitize(originalFileName);
        String token = UUID.randomUUID().toString();
        String objectBase = "%s/%d/%s-%s".formatted(IMPORTS_PREFIX, importId, token, safeName);
        String stagingKey = "%s/%d/%s-%s".formatted(STAGING_PREFIX, importId, token, safeName);

        long objectSize = size > 0 ? size : -1;
        String normalizedContentType = normalizeContentType(contentType);
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucket)
            .object(stagingKey)
            .stream(inputStream, objectSize, PART_SIZE)
            .contentType(normalizedContentType)
            .build());

        return new PreparedImportFile(
            stagingKey,
            objectBase,
            size,
            normalizedContentType,
            originalFileName);
    }

    public void registerTxSynchronization(PreparedImportFile preparedFile) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                try {
                    commit(preparedFile);
                } catch (Exception ex) {
                    throw new IllegalStateException("Failed to commit file to object storage", ex);
                }
            }

            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    try {
                        rollback(preparedFile);
                    } catch (Exception ex) {
                        log.warn("Failed to rollback import file {}", preparedFile.stagingKey(), ex);
                    }
                }
            }
        });
    }

    public void abortPreparedFile(PreparedImportFile preparedFile) {
        try {
            rollback(preparedFile);
        } catch (Exception ex) {
            log.warn("Failed to abort import file {}", preparedFile.stagingKey(), ex);
        }
    }

    public InputStream getObject(String objectKey) throws Exception {
        ensureBucket();
        return minioClient.getObject(GetObjectArgs.builder()
            .bucket(bucket)
            .object(objectKey)
            .build());
    }

    public boolean objectExists(String objectKey) throws Exception {
        ensureBucket();
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build());
            return true;
        } catch (ErrorResponseException ex) {
            String code = ex.errorResponse().code();
            if ("NoSuchKey".equalsIgnoreCase(code) || "NoSuchObject".equalsIgnoreCase(code)) {
                return false;
            }
            throw ex;
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
            .bucket(bucket)
            .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucket)
                .build());
        }
    }

    private void commit(PreparedImportFile prepared) throws Exception {
        ensureBucket();
        minioClient.copyObject(CopyObjectArgs.builder()
            .bucket(bucket)
            .object(prepared.finalKey())
            .source(CopySource.builder()
                .bucket(bucket)
                .object(prepared.stagingKey())
                .build())
            .build());
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(bucket)
            .object(prepared.stagingKey())
            .build());
    }

    private void rollback(PreparedImportFile prepared) throws Exception {
        ensureBucket();
        removeIfExists(prepared.stagingKey());
        removeIfExists(prepared.finalKey());
    }

    private void removeIfExists(String objectKey) throws Exception {
        if (!objectExists(objectKey)) {
            return;
        }
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(bucket)
            .object(objectKey)
            .build());
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "application/octet-stream";
        }
        return contentType.toLowerCase(Locale.ROOT);
    }
}
