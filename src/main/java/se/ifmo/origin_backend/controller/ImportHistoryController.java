package se.ifmo.origin_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.ifmo.origin_backend.dto.ImportOperationDTO;
import se.ifmo.origin_backend.dto.PageDTO;
import se.ifmo.origin_backend.service.ImportFileService;
import se.ifmo.origin_backend.service.ImportOperationListingService;
import se.ifmo.origin_backend.storage.FileNameSanitizer;

@RestController
@RequestMapping("/imports")
@RequiredArgsConstructor
public class ImportHistoryController {
    private final ImportOperationListingService service;
    private final ImportFileService fileService;

    @GetMapping
    public PageDTO<ImportOperationDTO> listImports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        return service.getPage(page, size);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable long id) throws Exception {
        var download = fileService.loadImportFile(id);
        String safeName = FileNameSanitizer.sanitize(download.fileName());
        String contentType = download.contentType() == null || download.contentType().isBlank()
            ? "application/octet-stream"
            : download.contentType();

        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(safeName)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType(contentType));

        if (download.size() != null && download.size() >= 0) {
            builder.contentLength(download.size());
        }

        return builder.body(new InputStreamResource(download.stream()));
    }
}
