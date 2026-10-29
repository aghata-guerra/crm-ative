package com.base.CRM.service;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.DiagnosticoCreateDto;
import com.base.CRM.entity.dto.DiagnosticoDto;
import com.base.CRM.exception.DiagnosticoNotFoundException;
import com.base.CRM.repository.DiagnosticoRepository;
import org.springframework.stereotype.Service;

@Service
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
        User user = userService.findUserById(createDto.userId());

        return DiagnosticoDto.fromEntity(diagnosticoRepository.save(createDto.toEntity(negociacao, user)));

    }

    // Find by Id

    public Diagnostico findDiagnosticoById(Long id){
        return diagnosticoRepository.findById(id).orElseThrow(DiagnosticoNotFoundException::new);
    }

    // Update diagnostico

    public DiagnosticoDto updateDiagnostico(Long id, DiagnosticoCreateDto createDto){

        Diagnostico diagnostico = findDiagnosticoById(id);

        diagnostico.setCompanySize(createDto.companySize());
        diagnostico.setCurrentSituation(createDto.currentSituation());
        diagnostico.setMainDifficulty(createDto.mainDifficulty());
        diagnostico.setNeeds(createDto.needs());

        return DiagnosticoDto.fromEntity(
                diagnosticoRepository.save(diagnostico)
        );


    }


}
