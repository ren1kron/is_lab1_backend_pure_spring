package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.error.RowError;

import java.util.Collections;
import java.util.List;

public record ImportResult(
    boolean success,
    int createdCount,
    List<RowError> errors) {
    public ImportResult(int createdCount) {
        this(true, createdCount, Collections.emptyList());
    }
}
