package se.ifmo.origin_backend.dto;

import java.util.List;

public record PageDTO<T>(List<T> items, int page, int size, long total) {}
