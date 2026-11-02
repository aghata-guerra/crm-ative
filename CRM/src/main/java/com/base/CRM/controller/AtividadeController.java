package com.base.CRM.controller;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.AtividadeCreateDto;
import com.base.CRM.entity.dto.AtividadeDto;
import com.base.CRM.service.AtividadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atividade")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @PostMapping
    public ResponseEntity<AtividadeDto> createAtividade(
            @RequestBody AtividadeCreateDto atividadeCreateDto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atividadeService.createAtividade(atividadeCreateDto, user));
    }

    @GetMapping("/negociacao/{negociacaoId}")
    public ResponseEntity<List<AtividadeDto>> findAllAtividadesByNegociacaoId(
            @PathVariable Long negociacaoId) {

        return ResponseEntity.ok(
                atividadeService.findAllAtividadesByNegociacaoId(negociacaoId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeDto> findAtividadeById(@PathVariable Long id){
        return ResponseEntity.ok(
                atividadeService.findAtividadeById(id)
        );
    }
}

