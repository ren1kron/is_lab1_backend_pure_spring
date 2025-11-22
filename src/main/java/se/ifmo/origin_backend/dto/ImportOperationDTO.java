package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.model.file_import.ImportStatus;

import java.time.Instant;

public record ImportOperationDTO(
    Long id,
    ImportStatus status,
    Integer createdCount,
    String fileName,
    Instant startedAt,
    Instant finishedAt) {}
