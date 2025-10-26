package se.ifmo.origin_backend.dto;

import se.ifmo.origin_backend.model.OrganizationType;

import java.time.LocalDate;
import java.util.List;

public record OrgSearchRequestDTO(
    String name,
    List<OrganizationType> types,
    Long employeesMin,
    Long employeesMax,
    Long annualTurnoverMin,
    Long annualTurnoverMax,
    Integer ratingMin,
    Integer ratingMax,
    LocalDate createdFrom,
    LocalDate createdTo,
    Long coordXMin,
    Long coordXMax,
    Long coordYMin,
    Long coordYMax,
    Float locationXMin,
    Float locationXMax,
    Integer locationYMin,
    Integer locationYMax,
    Float locationZMin,
    Float locationZMax) {}
