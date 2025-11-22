package se.ifmo.origin_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.ifmo.origin_backend.dto.ImportOperationDTO;
import se.ifmo.origin_backend.dto.PageDTO;
import se.ifmo.origin_backend.service.ImportOperationListingService;

@RestController
@RequestMapping("/imports")
@RequiredArgsConstructor
public class ImportHistoryController {
    private final ImportOperationListingService service;

    @GetMapping
    public PageDTO<ImportOperationDTO> listImports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        return service.getPage(page, size);
    }
}
