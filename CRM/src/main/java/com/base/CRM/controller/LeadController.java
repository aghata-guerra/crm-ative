
package com.base.CRM.controller;

import com.base.CRM.entity.dto.LeadCreateDto;
import com.base.CRM.entity.dto.LeadDto;
import com.base.CRM.service.LeadService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lead")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    // Listar todos os leads com paginacao
    @GetMapping
    public Page<LeadDto> getAllLeads(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
    ) {
        return leadService.findAllLeads(pageable);
    }

    // Buscar lead pelo ID
    @GetMapping("/{id}")
    public LeadDto findLeadById(@PathVariable Long id) {
        return LeadDto.fromEntity(
                leadService.findLeadById(id)
        );
    }

    // Buscar leads pelo ID da empresa
    @GetMapping("/empresa/{id}")
    public List<LeadDto> findLeadByEmpresaId(@PathVariable Long id) {
        return leadService.findAllLeadsByCompanyId(id);
    }

    // Cadastrar novo lead
    @PostMapping
    public ResponseEntity<LeadDto> createLead(
            @RequestBody LeadCreateDto createDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leadService.createLead(createDto));
    }
}
