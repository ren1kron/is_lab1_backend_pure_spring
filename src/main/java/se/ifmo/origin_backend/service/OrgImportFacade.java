package se.ifmo.origin_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.ifmo.origin_backend.dto.ImportResult;
import se.ifmo.origin_backend.error.ImportValidationException;
import se.ifmo.origin_backend.model.file_import.ImportOperation;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class OrgImportFacade {
    private final OrgImportService importService;
    private final ImportHistoryService historyService;

    public ImportResult importWithHistory(String fileName, InputStream inputStream) throws IOException {
        ImportOperation op = historyService.startImport(fileName);

        try {
            ImportResult result = importService.importOrganizations(inputStream);

            historyService.markSuccess(op.getId(), result.createdCount());

            return result;
        } catch (ImportValidationException ex) {
            historyService.markFailed(op.getId(), summarizeErrors(ex));
            throw ex;
        } catch (Exception ex) {
            historyService.markFailed(op.getId(), ex.getMessage());
            throw ex;
        }
    }

    private String summarizeErrors(ImportValidationException ex) {
        return ex.getErrors().stream()
            .limit(10)
            .map(err -> "row %d: %s".formatted(err.row(), err.message()))
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
    }
}
