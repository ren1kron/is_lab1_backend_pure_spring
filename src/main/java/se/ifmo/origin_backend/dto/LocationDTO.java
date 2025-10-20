package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.Size;

public record LocationDTO(
    float x,
    int y,
    float z,
    @Size(max = 63) String name) {}
