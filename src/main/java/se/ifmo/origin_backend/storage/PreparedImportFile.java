package se.ifmo.origin_backend.storage;

public record PreparedImportFile(
    String stagingKey,
    String finalKey,
    long size,
    String contentType,
    String originalFileName) {}
