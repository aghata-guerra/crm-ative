package com.base.CRM.service;

import com.base.CRM.config.CrmAccess;
import com.base.CRM.entity.Atividade;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.entity.dto.NegociacaoCreateDto;
import com.base.CRM.entity.dto.NegociacaoDto;
import com.base.CRM.entity.dto.NegociacoesPorOrigemDto;
import com.base.CRM.enumClass.*;
import com.base.CRM.exception.*;
import com.base.CRM.repository.NegociacaoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NegociacaoService {
    private final NegociacaoRepository repository;
    private final LeadService leads;
    private final UserService users;
    private final BalanceService balance;

    public NegociacaoService(NegociacaoRepository repository, LeadService leads,
                             UserService users, BalanceService balance) {
        this.repository = repository;
        this.leads = leads;
        this.users = users;
        this.balance = balance;
    }

    public NegociacaoDto createNegociacao(NegociacaoCreateDto dto) {
        CrmAccess.requireUser(dto.userId());
        return NegociacaoDto.fromEntity(repository.save(dto.toEntity(
            leads.findLeadById(dto.leadId()), users.findUserById(dto.userId()))));
    }

    public Negociacao findNegociacaoById(Long id) {
        var deal = repository.findById(id).orElseThrow(NegociacaoNotFoundException::new);
        CrmAccess.requireDeal(deal);
        return deal;
    }

    public NegociacaoDto getNegociacao(Long id) {
        return NegociacaoDto.fromEntity(findNegociacaoById(id));
    }

    public List<NegociacaoDto> findAllNegociacaoByLeadId(Long id) {
        return repository.findByLead_Id(id).stream().filter(CrmAccess::canView)
            .map(NegociacaoDto::fromEntity).toList();
    }

    public List<NegociacaoDto> findAllNegociacaoByUserId(Long id) {
        CrmAccess.requireUser(id);
        return repository.findByUser_Id(id).stream().map(NegociacaoDto::fromEntity).toList();
    }

    public List<NegociacaoDto> findAllNegociacao() {
        return visibleDeals().stream().map(NegociacaoDto::fromEntity).toList();
    }

    private List<Negociacao> visibleDeals() {
        return CrmAccess.isManager() ? repository.findAll()
            : repository.findByUser_Id(CrmAccess.currentUser().getId());
    }

    public Page<NegociacaoDto> findAllNegociacao(Pageable page) {
        return findAllNegociacaoByEtapaAndStatusNegociacao(null, null, page);
    }

    public Page<NegociacaoDto> findAllNegociacaoByEtapaAndStatusNegociacao(
            Etapa stage, StatusNegociacao status, Pageable page) {
        var result = CrmAccess.isManager() ? repository.findByFilters(stage, status, page)
            : repository.findByFiltersAndUser(CrmAccess.currentUser().getId(), stage, status, page);
        return result.map(NegociacaoDto::fromEntity);
    }

    public Page<NegociacaoDto> findAllNegociacaoByEtapaAndStatusNegociacaoAndUserId(
            Etapa stage, StatusNegociacao status, Long userId, Pageable page) {
        CrmAccess.requireUser(userId);
        return repository.findByFiltersAndUser(userId, stage, status, page).map(NegociacaoDto::fromEntity);
    }

    private Page<NegociacaoDto> paginate(List<Negociacao> items, Pageable page) {
        int start = (int) Math.min(page.getOffset(), items.size());
        int end = Math.min(start + page.getPageSize(), items.size());
        return new PageImpl<>(items.subList(start, end).stream().map(NegociacaoDto::fromEntity).toList(),
            page, items.size());
    }

    public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatus(
            Integer days, StatusNegociacao status, Pageable page) {
        if (days == null || days < 1) throw new IllegalArgumentException("Informe um prazo de pelo menos um dia.");
        var limit = LocalDate.now().minusDays(days);
        return paginate(visibleDeals().stream().filter(deal -> deal.getStatus() == status)
            .filter(deal -> !deal.getLastMovementDate().isAfter(limit)).toList(), page);
    }

    public Page<NegociacaoDto> findAllNegociacaoWithDeadLineAndByStatusAndUserId(
            Integer days, StatusNegociacao status, Long userId, Pageable page) {
        CrmAccess.requireUser(userId);
        if (days == null || days < 1) throw new IllegalArgumentException("Informe um prazo de pelo menos um dia.");
        var limit = LocalDate.now().minusDays(days);
        return paginate(repository.findByUser_Id(userId).stream().filter(deal -> deal.getStatus() == status)
            .filter(deal -> !deal.getLastMovementDate().isAfter(limit)).toList(), page);
    }

    private boolean withoutPendingStep(Negociacao deal) {
        return deal.getStatus() == StatusNegociacao.EM_NEGOCIACAO
            && deal.getProximoPasso().stream().noneMatch(step -> step.getStatusProximoPasso() != StatusProximoPasso.REALIZADA);
    }

    public Page<NegociacaoDto> findAllNegociacaoWithoutProximoPassoAndEmNegociacao(Pageable page) {
        return paginate(visibleDeals().stream().filter(this::withoutPendingStep).toList(), page);
    }

    public List<NegociacaoDto> findAllNegociacaoWithoutProximoPassoAndUserId(Long userId) {
        CrmAccess.requireUser(userId);
        return repository.findByUser_Id(userId).stream().filter(this::withoutPendingStep)
            .map(NegociacaoDto::fromEntity).toList();
    }

    public void requireOpen(Negociacao deal) {
        if (!verifyIfNegociacaoIsValid(deal)) throw new NegociacaoClosedException();
    }

    private void record(Negociacao deal, String description) {
        deal.getAtividadeList().add(new Atividade(null, deal, CrmAccess.currentUser(),
            Tipo.OBSERVACAO, description, LocalDate.now()));
    }

    public NegociacaoDto updateStageNegociacao(Long id, Etapa stage, User actor) {
        if (stage == null) throw new IllegalArgumentException("Selecione uma etapa válida.");
        var deal = findNegociacaoById(id);
        requireOpen(deal);
        if (deal.getStage() == stage) throw new IllegalArgumentException("A oportunidade já está nesta etapa.");
        record(deal, "Mudança de etapa: " + deal.getStage() + " → " + stage);
        deal.setStage(stage);
        deal.setUpdatedStageDate(LocalDate.now());
        return NegociacaoDto.fromEntity(repository.save(deal));
    }

    public NegociacaoDto nextStageUpdate(Long id, User actor) {
        var deal = findNegociacaoById(id);
        requireOpen(deal);
        var next = switch (deal.getStage()) {
            case NOVO_LEAD -> Etapa.QUALIFICACAO;
            case QUALIFICACAO -> Etapa.DIAGNOSTICO;
            case DIAGNOSTICO -> Etapa.PROPOSTA;
            case PROPOSTA -> throw new StageChangeException();
        };
        return updateStageNegociacao(id, next, actor);
    }

    public NegociacaoDto updateStatusNegociacao(Long id, StatusNegociacao status) {
        CrmAccess.requireManager();
        if (status == StatusNegociacao.ENCERRADA_GANHA) return finishNegociacaoAsGain(id);
        throw new IllegalArgumentException("Use o encerramento com motivo para perdas. Negociações encerradas não podem ser reabertas.");
    }

    public NegociacaoDto finishNegociacaoAsGain(Long id) {
        var deal = findNegociacaoById(id);
        requireOpen(deal);
        deal.setStatus(StatusNegociacao.ENCERRADA_GANHA);
        deal.setFinalDate(LocalDate.now());
        record(deal, "Oportunidade encerrada como ganha.");
        balance.addBalanceEarnings(deal.getEstimatedCost());
        return NegociacaoDto.fromEntity(repository.save(deal));
    }

    public NegociacaoDto finishNegociacaoAsLoss(Long id, String reason) {
        if (reason == null || reason.isBlank() || reason.length() > 255) {
            throw new IllegalArgumentException("Informe um motivo de perda com até 255 caracteres.");
        }
        var deal = findNegociacaoById(id);
        requireOpen(deal);
        deal.setStatus(StatusNegociacao.ENCERRADA_PERDA);
        deal.setFinalDate(LocalDate.now());
        deal.setLossReason(reason.trim());
        record(deal, "Oportunidade encerrada como perdida. Consulte o motivo no fechamento.");
        balance.subtractBalanceEarnings(deal.getEstimatedCost().negate());
        return NegociacaoDto.fromEntity(repository.save(deal));
    }

    public List<NegociacoesPorOrigemDto> countNegociacaoByOrigin() {
        CrmAccess.requireManager();
        return repository.countNegociacoesPorOrigem();
    }

    public Boolean verifyIfNegociacaoIsValid(Negociacao deal) {
        return deal.getStatus() == StatusNegociacao.EM_NEGOCIACAO;
    }
}
