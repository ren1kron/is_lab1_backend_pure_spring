package se.ifmo.origin_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.ifmo.origin_backend.dto.ImportResult;
import se.ifmo.origin_backend.error.ImportValidationException;
import se.ifmo.origin_backend.model.file_import.ImportOperation;
import se.ifmo.origin_backend.storage.ObjectStorageService;
import se.ifmo.origin_backend.storage.PreparedImportFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class OrgImportFacade {
    private final OrgImportService importService;
    private final ImportHistoryService historyService;
    private final ObjectStorageService storageService;

    public ImportResult importWithHistory(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename();
        String fileName = (originalName == null || originalName.isBlank()) ? "import.json" : originalName;
        ImportOperation op = historyService.startImport(fileName);

        PreparedImportFile prepared;
        try (InputStream uploadStream = file.getInputStream()) {
            prepared = storageService.prepareImportFile(
                op.getId(),
                fileName,
                uploadStream,
                file.getSize(),
                file.getContentType());
        } catch (Exception ex) {
            historyService.markFailed(op.getId(), "Failed to save file: " + ex.getMessage());
            throw ex;
        }

        ImportResult result;
        try (InputStream importStream = file.getInputStream()) {
            result = importService.importOrganizations(importStream, prepared);
        } catch (ImportValidationException ex) {
            storageService.abortPreparedFile(prepared);
            historyService.markFailed(op.getId(), summarizeErrors(ex));
            throw ex;
        } catch (Exception ex) {
            storageService.abortPreparedFile(prepared);
            historyService.markFailed(op.getId(), ex.getMessage());
            throw ex;
        }

        try {
            historyService.markSuccess(
                op.getId(),
                result.createdCount(),
                prepared.finalKey(),
                prepared.size(),
                prepared.contentType());
        } catch (Exception ex) {
            historyService.markFailed(op.getId(), ex.getMessage());
            throw ex;
        }

        return result;
    }

    private String summarizeErrors(ImportValidationException ex) {
        return ex.getErrors().stream()
            .limit(10)
            .map(err -> "row %d: %s".formatted(err.row(), err.message()))
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
    }
}
