package com.base.CRM.controller;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.ProximoPassoCreateDto;
import com.base.CRM.entity.dto.ProximoPassoDto;
import com.base.CRM.enumClass.StatusProximoPasso;
import com.base.CRM.service.ProximoPassoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/proximopasso")
public class ProximoPassoController {

    private final ProximoPassoService proximoPassoService;

    public ProximoPassoController(ProximoPassoService proximoPassoService) {
        this.proximoPassoService = proximoPassoService;
    }

    @PostMapping
    public ResponseEntity<ProximoPassoDto> createProximoPasso(
            @Valid @RequestBody ProximoPassoCreateDto proximoPassoCreateDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proximoPassoService.createProximoPasso(proximoPassoCreateDto, user));

    }

    @PatchMapping("/{proximoPassoId}/status")
    public ResponseEntity<ProximoPassoDto> changeStatusProximoPasso(
            @PathVariable Long proximoPassoId,
            @RequestBody StatusProximoPasso statusProximoPasso
    ) {
        return ResponseEntity.ok(
                proximoPassoService.changeStatusProximoPasso(
                        proximoPassoId,
                        statusProximoPasso
                )
        );
    }

    @GetMapping("/negociacao/{negociacaoId}")
    public ResponseEntity<List<ProximoPassoDto>> findProximoPassoByNegociacaoId(
            @PathVariable Long negociacaoId
    ) {
        return ResponseEntity.ok(
                proximoPassoService.findProximoPassoByNegociacaoId(negociacaoId)
        );
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ProximoPassoDto>> findByExpectedDateBetweenAndStatus(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam StatusProximoPasso status
    ) {
        return ResponseEntity.ok(
                proximoPassoService.findByExpectedDateBetweenAndStatus(
                        startDate,
                        endDate,
                        status
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProximoPassoDto>> findByProximoPassoByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                proximoPassoService.findByProximoPassoByUserId(userId)
        );
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<ProximoPassoDto>> findByProximoPassoByAuthUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                proximoPassoService.findByProximoPassoByUserId(user.getId())
        );
    }

    @PatchMapping("/{proximoPassoId}/data")
    public ResponseEntity<ProximoPassoDto> alterDateNextContact(
            @PathVariable Long proximoPassoId,
            @RequestBody LocalDate nextContact
    ) {
        return ResponseEntity.ok(
                proximoPassoService.alterDateNextContact(
                        nextContact,
                        proximoPassoId
                )
        );
    }



}
