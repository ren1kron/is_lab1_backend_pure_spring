package se.ifmo.origin_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.origin_backend.dto.ImportOperationDTO;
import se.ifmo.origin_backend.dto.PageDTO;
import se.ifmo.origin_backend.model.file_import.ImportOperation;
import se.ifmo.origin_backend.repo.ImportOperationRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOperationListingService {
    private final ImportOperationRepo repo;

    @Transactional(readOnly = true)
    public PageDTO<ImportOperationDTO> getPage(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100);

        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "startedAt"));

        Page<ImportOperation> p = repo.findAll(pageable);

        List<ImportOperationDTO> items = p.getContent().stream()
            .map(this::toDto)
            .toList();

        return new PageDTO<>(
            items,
            p.getNumber(),
            p.getSize(),
            p.getTotalElements());
    }

    private ImportOperationDTO toDto(ImportOperation op) {
        return new ImportOperationDTO(
            op.getId(),
            op.getStatus(),
            op.getCreatedCount(),
            op.getFileName(),
            op.getStartedAt(),
            op.getFinishedAt());
    }
}
