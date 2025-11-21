package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.Size;

public record LocationDTO(
    float x,
    int y,
    float z,
    @Size(max = 63) String name) {
    public String fieldsToString() {
        return String.format("x = %f, y = %d, z = %f, name = %s", x, y, z, name);
    }
}
