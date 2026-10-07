package com.tms.controller;

import com.tms.dto.CategoriesRequest;
import com.tms.dto.LabelsRequest;
import com.tms.service.MetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(metadataService.getCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<List<String>> replaceCategories(@RequestBody CategoriesRequest req) {
        return ResponseEntity.ok(metadataService.replaceCategories(req.getCategories()));
    }

    @GetMapping("/labels")
    public ResponseEntity<List<String>> getLabels() {
        return ResponseEntity.ok(metadataService.getLabels());
    }

    @PostMapping("/labels")
    public ResponseEntity<List<String>> replaceLabels(@RequestBody LabelsRequest req) {
        return ResponseEntity.ok(metadataService.replaceLabels(req.getLabels()));
    }
}

