package com.base.CRM.service;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.DiagnosticoCreateDto;
import com.base.CRM.entity.dto.DiagnosticoDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.exception.DiagnosticoNotFoundException;
import com.base.CRM.repository.DiagnosticoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class DiagnosticoService {

    private final DiagnosticoRepository diagnosticoRepository;
    private final UserService userService;
    private final NegociacaoService negociacaoService;

    public DiagnosticoService(DiagnosticoRepository diagnosticoRepository, UserService userService, NegociacaoService negociacaoService) {
        this.diagnosticoRepository = diagnosticoRepository;
        this.userService = userService;
        this.negociacaoService = negociacaoService;
    }

    // Create a diagnostico

    public DiagnosticoDto createDiagnostico(DiagnosticoCreateDto createDto){

        Negociacao negociacao = negociacaoService.findNegociacaoById(createDto.negociacaoId());
        negociacaoService.requireOpen(negociacao);
        User user = com.base.CRM.config.CrmAccess.currentUser();

        return DiagnosticoDto.fromEntity(diagnosticoRepository.save(createDto.toEntity(negociacao, user)));

    }

    // Find by Id

    public Diagnostico findDiagnosticoById(Long id){
        Diagnostico result = diagnosticoRepository.findById(id).orElseThrow(DiagnosticoNotFoundException::new);
        negociacaoService.findNegociacaoById(result.getNegociacao().getId());
        return result;
    }

    // Update diagnostico

    public DiagnosticoDto updateDiagnostico(Long id, DiagnosticoCreateDto createDto){

        Diagnostico diagnostico = findDiagnosticoById(id);

        negociacaoService.requireOpen(diagnostico.getNegociacao());
        if (!diagnostico.getNegociacao().getId().equals(createDto.negociacaoId())) {
            throw new IllegalArgumentException("O diagnóstico pertence a outra negociação.");
        }
        diagnostico.setCompanySize(createDto.companySize());
        diagnostico.setCurrentSituation(createDto.currentSituation());
        diagnostico.setMainDifficulty(createDto.mainDifficulty());
        diagnostico.setNeeds(createDto.needs());

        return DiagnosticoDto.fromEntity(
                diagnosticoRepository.save(diagnostico)
        );

    }

    public Page<DiagnosticoDto> findAllDiagnostico(Pageable pageable){

        var result = com.base.CRM.config.CrmAccess.isManager()
            ? diagnosticoRepository.findAll(pageable)
            : diagnosticoRepository.findByNegociacao_User_Id(com.base.CRM.config.CrmAccess.currentUser().getId(), pageable);
        return result.map(
                DiagnosticoDto::fromEntity
        );
    }


}
