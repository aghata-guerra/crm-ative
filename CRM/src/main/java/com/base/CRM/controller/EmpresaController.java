package com.base.CRM.controller;

import com.base.CRM.entity.dto.EmpresaCreateDto;
import com.base.CRM.entity.dto.EmpresaDto;
import com.base.CRM.service.EmpresaService;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public Page<EmpresaDto> getAllEmpresas(
            @PageableDefault(page = 0, size = 10, sort = "companyName") Pageable pageable
    ){
        return empresaService.findAllEmpresas(pageable);
    }

    @GetMapping("/{id}")
    public EmpresaDto getEmpresaById(@PathVariable Long id){
        return EmpresaDto.fromEntity(empresaService.findEmpresaById(id));
    }

    @GetMapping("/{cnpj}")
    public EmpresaDto getEmpresaByCnpj(@PathVariable String cnpj){
        return empresaService.findEmpresaByCnpj(cnpj);
    }

    @PostMapping("/create/{cnpj}")
    public ResponseEntity<EmpresaDto> createEmpresa(@PathVariable String cnpj) throws IOException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresaService.createEmpresa(cnpj));
    }

    @PostMapping("/create")
    public ResponseEntity<EmpresaDto> createEmpresaManual(@RequestBody EmpresaCreateDto createDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresaService.createEmpresaManual(createDto));
    }





}
