package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record CoordinatesDTO(
    @Max(562) @NotNull Long x,
    @NotNull Long y) {}
