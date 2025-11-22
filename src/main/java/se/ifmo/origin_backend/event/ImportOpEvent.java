package se.ifmo.origin_backend.event;

import se.ifmo.origin_backend.model.file_import.ImportStatus;

public record ImportOpEvent(
    Long id,
    ImportStatus status,
    Integer createdCount) {}
