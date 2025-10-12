package se.ifmo.origin_backend.dto;

import jakarta.validation.constraints.Size;

public record AddressDTO(@Size(max = 63) String street) {}
