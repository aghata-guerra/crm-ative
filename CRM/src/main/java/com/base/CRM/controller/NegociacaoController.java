package com.base.CRM.controller;

import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.LossReasonRequest;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.StatusNegociacao;
import com.base.CRM.service.NegociacaoService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
            @RequestBody NegociacaoCreateDto negociacaoCreateDto
            ){

        return ResponseEntity.ok(
                negociacaoService.createNegociacao(negociacaoCreateDto));
    }

    @GetMapping("/{id}")
    public NegociacaoDto getNegociacaoById(@PathVariable Long id){
        return NegociacaoDto.fromEntity(negociacaoService.findNegociacaoById(id));
    }

    @GetMapping
    public Page<NegociacaoDto> getAllNegociacao(
            @PageableDefault(page = 0, size = 10, sort = "companyName") Pageable pageable
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


    @GetMapping("/etapa")
    public ResponseEntity<List<NegociacaoDto>> getAllNegociacaoByEtapa(
            @RequestParam Etapa stage) {

        return ResponseEntity.ok(
                negociacaoService.getAllNegociacaoByEtapa(stage)
        );
    }

    @GetMapping("/prazo")
    public ResponseEntity<List<NegociacaoDto>> findAllNegociacaoWithDeadLineAndByStatus(
            @RequestParam Integer daysOnHold,
            @RequestParam StatusNegociacao status) {

        return ResponseEntity.ok(
                negociacaoService.findAllNegociacaoWithDeadLineAndByStatus(daysOnHold, status)
        );
    }

    @GetMapping("/sem-proximo-passo")
    public ResponseEntity<List<NegociacaoDto>> findAllNegociacaoWithoutProximoPasso(){
        return ResponseEntity.ok(
                negociacaoService.findAllNegociacaoWithoutProximoPasso()
        );
    }

    @PatchMapping("/{negociacaoId}/finalizar/ganha")
    public ResponseEntity<NegociacaoDto> finishNegociacaoAsGain(
            @PathVariable Long negociacaoId) {

        return ResponseEntity.ok(
                negociacaoService.finishNegociacaoAsGain(negociacaoId)
        );
    }

    @PatchMapping("/{negociacaoId}/finalizar/perca")
    public ResponseEntity<NegociacaoDto> finishNegociacaoAsLoss(
            @PathVariable Long negociacaoId) {

        return ResponseEntity.ok(
                negociacaoService.finishNegociacaoAsGain(negociacaoId)
        );
    }

    @PatchMapping("/{negociacaoId}/finalizar/perda")
    public ResponseEntity<NegociacaoDto> finishNegociacaoAsLoss(
            @PathVariable Long negociacaoId,
            @RequestBody LossReasonRequest request) {

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
