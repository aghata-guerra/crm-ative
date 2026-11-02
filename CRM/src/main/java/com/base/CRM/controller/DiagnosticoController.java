package com.base.CRM.controller;

import com.base.CRM.entity.dto.DiagnosticoCreateDto;
import com.base.CRM.entity.dto.DiagnosticoDto;
import com.base.CRM.service.DiagnosticoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    private final DiagnosticoService diagnosticoService;

    public DiagnosticoController(DiagnosticoService diagnosticoService) {
        this.diagnosticoService = diagnosticoService;
    }


    // Criar diagnóstico
    @PostMapping
    public ResponseEntity<DiagnosticoDto> createDiagnostico(
            @Valid @RequestBody DiagnosticoCreateDto createDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diagnosticoService.createDiagnostico(createDto));
    }

    // Buscar diagnóstico por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findDiagnosticoById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                DiagnosticoDto.fromEntity(
                        diagnosticoService.findDiagnosticoById(id)
                )
        );
    }

    // Atualizar diagnóstico
    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticoDto> updateDiagnostico(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosticoCreateDto createDto
    ) {
        return ResponseEntity.ok(
                diagnosticoService.updateDiagnostico(id, createDto)
        );
    }

    // Listar todos os diagnósticos com paginação
    @GetMapping
    public ResponseEntity<Page<DiagnosticoDto>> findAllDiagnostico(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                diagnosticoService.findAllDiagnostico(pageable)
        );
    }
}

