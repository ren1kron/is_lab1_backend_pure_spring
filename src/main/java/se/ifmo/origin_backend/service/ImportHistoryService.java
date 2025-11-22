package se.ifmo.origin_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.event.ImportOpEvent;
import se.ifmo.origin_backend.model.file_import.ImportOperation;
import se.ifmo.origin_backend.model.file_import.ImportStatus;
import se.ifmo.origin_backend.repo.ImportOperationRepo;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ImportHistoryService {
    private final ImportOperationRepo repo;
    private final ApplicationEventPublisher events;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportOperation startImport(String fileName) {
        ImportOperation op = new ImportOperation();
        op.setStatus(ImportStatus.IN_PROGRESS);
        op.setFileName(fileName);
        ImportOperation saved = repo.save(op);

        events.publishEvent(new ImportOpEvent(
            saved.getId(),
            saved.getStatus(),
            null));

        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long id, int createdCount) {
        ImportOperation op = repo.findById(id).orElseThrow();
        op.setStatus(ImportStatus.SUCCESS);
        op.setCreatedCount(createdCount);
        op.setFinishedAt(Instant.now());

        events.publishEvent(new ImportOpEvent(
            op.getId(),
            op.getStatus(),
            op.getCreatedCount()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id, String errorMessage) {
        ImportOperation op = repo.findById(id).orElseThrow();
        op.setStatus(ImportStatus.ERROR);
        op.setFinishedAt(Instant.now());
        op.setErrorMessage(errorMessage);

        events.publishEvent(new ImportOpEvent(
            op.getId(),
            op.getStatus(),
            null));
    }
}
