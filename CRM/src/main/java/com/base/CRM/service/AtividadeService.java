package com.base.CRM.service;

import com.base.CRM.repository.AtividadeRepository;
import org.springframework.stereotype.Service;

@Service
public class AtividadeService {

  private final AtividadeRepository atividadeRepository;

  public AtividadeService(AtividadeRepository atividadeRepository) {
    this.atividadeRepository = atividadeRepository;
  }

}
