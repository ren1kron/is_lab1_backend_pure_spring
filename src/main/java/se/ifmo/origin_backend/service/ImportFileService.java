package se.ifmo.origin_backend.service;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.ifmo.origin_backend.model.file_import.ImportOperation;
import se.ifmo.origin_backend.repo.ImportOperationRepo;
import se.ifmo.origin_backend.storage.ObjectStorageService;

@Service
@RequiredArgsConstructor
public class ImportFileService {
    private final ImportOperationRepo repo;
    private final ObjectStorageService storageService;

    @Transactional(readOnly = true)
    public ImportFileDownload loadImportFile(long id) throws Exception {
        ImportOperation op = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Import not found"));
        if (op.getFileObjectKey() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File is not available");
        }
        if (!storageService.objectExists(op.getFileObjectKey())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        InputStream stream = storageService.getObject(op.getFileObjectKey());
        return new ImportFileDownload(
            stream,
            op.getFileName(),
            op.getFileContentType(),
            op.getFileSize());
    }

    public record ImportFileDownload(
        InputStream stream,
        String fileName,
        String contentType,
        Long size) {}
}
