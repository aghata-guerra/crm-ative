package com.base.CRM.controller;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.LossReasonRequest;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import com.base.CRM.service.NegociacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/negociacao")
public class NegociacaoController {

    private final NegociacaoService negociacaoService;

    public NegociacaoController(NegociacaoService negociacaoService) {
        this.negociacaoService = negociacaoService;
    }

    @PostMapping
    public ResponseEntity<NegociacaoDto> createNegociacao(
            @RequestBody @Valid NegociacaoCreateDto negociacaoCreateDto
            ){

        return ResponseEntity.ok(
                negociacaoService.createNegociacao(negociacaoCreateDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegociacaoDto> getNegociacaoById(@PathVariable Long id){
        return ResponseEntity.ok(NegociacaoDto.fromEntity(negociacaoService.findNegociacaoById(id)));
    }

    @GetMapping
    public Page<NegociacaoDto> getAllNegociacao(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
    ) {
        return negociacaoService.findAllNegociacao(pageable);
    }


    // Apenas para Gestores
    @GetMapping("/user/{id}")
    public List<NegociacaoDto> getAllNegociacaoByUserId(@PathVariable Long id){
        return negociacaoService.findAllNegociacaoByUserId(id);
    }

    @GetMapping("/user")
    public List<NegociacaoDto> getAllNegociacaoByAuthUser(
            @AuthenticationPrincipal User user
    ){
        return negociacaoService.findAllNegociacaoByUserId(user.getId());
    }

    @GetMapping("/lead/{id}")
    public List<NegociacaoDto> getAllNegociacaoByLeadId(@PathVariable Long id){
        return negociacaoService.findAllNegociacaoByLeadId(id);
    }

    @PatchMapping("/{negociacaoId}/status")
    public ResponseEntity<NegociacaoDto> updateStatusNegociacao(
            @PathVariable Long negociacaoId,
            @RequestBody StatusNegociacao statusNegociacao
            ){

        return ResponseEntity.ok(
                negociacaoService.updateStatusNegociacao(negociacaoId, statusNegociacao)
        );

    }

    @PatchMapping("/{negociacaoId}/etapa")
    public ResponseEntity<NegociacaoDto> updateStageNegociacao(
            @PathVariable Long negociacaoId,
            @RequestBody Etapa etapa) {

        return ResponseEntity.ok(
                negociacaoService.updateStageNegociacao(negociacaoId, etapa)
        );
    }
    
    @PatchMapping("/{negociacaoId}/nextstage")
    public ResponseEntity<NegociacaoDto> nextStageUpdate(
        @PathVariable Long negociacaoId
    ) {
      return ResponseEntity.ok(
          negociacaoService.nextStageUpdate(negociacaoId)
      );
    }


    @GetMapping("/filter")
    public Page<NegociacaoDto> getAllNegociacaoByEtapa(
        @RequestParam(required = false) Etapa stage,
        @RequestParam(required = false) StatusNegociacao statusNegociacao,
        @AuthenticationPrincipal User user,
        @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        return negociacaoService.findAllNegociacaoByEtapaAndStatusNegociacao(stage, statusNegociacao, pageable);
        
    }

    @GetMapping("/filter/me")
    public Page<NegociacaoDto> findAllNegociacaoByEtapaAndStatusNegociacaoAndUserId(
        @RequestParam(required = false) Etapa stage,
        @RequestParam(required = false) StatusNegociacao statusNegociacao,
        @AuthenticationPrincipal User user,
        @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
        ) {
  
      return negociacaoService.findAllNegociacaoByEtapaAndStatusNegociacaoAndUserId(stage, statusNegociacao, user.getId(), pageable);
    }

    @GetMapping("/prazo")
    public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatus(
            @RequestParam Integer daysOnHold, // definir um tempo padrão com cliente
            @RequestParam StatusNegociacao status,
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        return negociacaoService.findAllNegociacaoWithDeadLineAndByStatus(daysOnHold, status, pageable);
    }

    @GetMapping("/prazo/me")
    public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatusAndUserId(
        @RequestParam Integer daysOnHold, // definir um tempo padrão com cliente
        @RequestParam StatusNegociacao status,
        @AuthenticationPrincipal User user,
        @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
  
      return negociacaoService.findAllNegociacaoWithDeadLineAndByStatusAndUserId(daysOnHold, status, user.getId(), pageable);
    }

    @GetMapping("/sem-proximo-passo")
    public Page<NegociacaoDto> getAllNegociacaoWithoutProximoPasso(
        @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
    ){
        return negociacaoService.findAllNegociacaoWithoutProximoPassoAndEmNegociacao(pageable);
    }

    @GetMapping("/sem-proximo-passo/me")
    public ResponseEntity<List<NegociacaoDto>> getAllNegociacaoWithoutProximoPassoAndUserId(
        @AuthenticationPrincipal User user
    ){
      return ResponseEntity.ok(
          negociacaoService.findAllNegociacaoWithoutProximoPassoAndUserId(user.getId())
      );
    }

    @PatchMapping("/{negociacaoId}/finalizar/ganha")
    public ResponseEntity<NegociacaoDto> finishNegociacaoAsGain(
            @PathVariable Long negociacaoId) {

        return ResponseEntity.ok(
                negociacaoService.finishNegociacaoAsGain(negociacaoId)
        );
    }
    
    @PatchMapping("/{negociacaoId}/finalizar/perda")
    public ResponseEntity<NegociacaoDto> finishNegociacaoAsLoss(
            @PathVariable Long negociacaoId,
            @RequestBody @Valid LossReasonRequest request) {

        return ResponseEntity.ok(
                negociacaoService.finishNegociacaoAsLoss(
                        negociacaoId,
                        request.lossReason()
                )
        );
    }

    @GetMapping("/relatorios/por-origem")
    public ResponseEntity<List<NegociacoesPorOrigemDto>> countNegociacaoByOrigin() {

        return ResponseEntity.ok(
                negociacaoService.countNegociacaoByOrigin()
        );
    }

}
