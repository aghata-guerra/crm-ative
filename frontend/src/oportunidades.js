import { useState } from 'react';
import { openDeal, alerts, date, today } from './indicadores';
import { Drawer } from './components';
import { SummaryCards, hasContact, daysIdle, opportunityInfo, pendingSteps } from './comercial';
import { origins, originNames } from './origens';
import { ActionForm, Field, Select, Table } from './components';

const stages = [
  ['NOVO_LEAD', 'Novo lead'], ['QUALIFICACAO', 'Qualificação'],
  ['DIAGNOSTICO', 'Diagnóstico'], ['PROPOSTA', 'Proposta'],
];
const lossReasons = ['Preço', 'Sem prioridade agora', 'Escolheu concorrente', 'Sem resposta', 'Não se encaixa no serviço', 'Outro'];
const statuses = [
  ['EM_NEGOCIACAO', 'Em negociação'], ['ENCERRADA_GANHA', 'Ganha'], ['ENCERRADA_PERDA', 'Perdida'],
];
const types = [['CONVERSA', 'Conversa'], ['REUNIAO', 'Reunião'], ['PROPOSTA', 'Proposta'], ['OBSERVACAO', 'Observação']];
const stageNames = Object.fromEntries(stages);
const statusNames = Object.fromEntries(statuses);
const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const isOpen = openDeal;
const pending = step => step.statusProximoPasso !== 'REALIZADA';

export function Funil({ data, save, onOpen, stalledDays = 7 }) {
  const [filter, setFilter] = useState('EM_NEGOCIACAO');
  const [stageFilter, setStageFilter] = useState('');
  const [search, setSearch] = useState('');
  const [view, setView] = useState('board');
  const [creating, setCreating] = useState(false);
  const [leadId, setLeadId] = useState('');
  const leads = new Map(data.leads.map(item => [item.id, item.name]));
  const origin = data.leads.find(item => item.id === Number(leadId))?.origin || 'DESCONHECIDA';
  const opened = data.oportunidades.filter(isOpen);
  const shown = data.oportunidades.filter(item => {
    const info = opportunityInfo(data, item);
    return (!filter || item.statusNegociacao === filter)
      && (!stageFilter || item.stage === stageFilter)
      && [info.title, info.contact].some(value => value.toLocaleLowerCase('pt-BR').includes(search.trim().toLocaleLowerCase('pt-BR')));
  });
  const summaries = [
    { label: 'Em andamento', value: opened.length, note: 'oportunidades abertas' },
    { label: 'Sem primeiro contato', value: opened.filter(item => !hasContact(item)).length, note: 'aguardando atendimento', attention: true },
    { label: 'Precisam de atenção', value: opened.filter(item => alerts(item, stalledDays).length).length, note: 'pendências ou inatividade', attention: true },
    { label: 'Valor em negociação', value: money.format(opened.reduce((sum, item) => sum + Number(item.estimatedCost || 0), 0)), note: 'estimativa de contrato' },
  ];
  function closeCreate() {
    setCreating(false);

  }
  function card(item) {
    const info = opportunityInfo(data, item);
    const notices = alerts(item, stalledDays);
    const firstStep = pendingSteps(item)[0];
    const idle = daysIdle(item);
    const needsContact = isOpen(item) && !hasContact(item);
    return <button type="button" className="deal" key={item.id} onClick={() => onOpen(item.id)}>
      <span className="deal-title"><strong>{info.title}</strong>
        {isOpen(item) && (needsContact || notices.length > 0) && <span className="badge badge-warning">{needsContact ? 'Responder' : 'Atenção'}</span>}
        {!isOpen(item) && <span className={'badge ' + (item.statusNegociacao === 'ENCERRADA_GANHA' ? 'badge-success' : 'badge-warning')}>{statusNames[item.statusNegociacao]}</span>}
      </span>
      <span className="deal-contact">{info.contact}</span>
      <span className="deal-meta"><span>{originNames[item.origin] || 'Desconhecida'}</span>
        <span>{info.responsible} · {idle === null ? 'Data não informada' : idle + 'd parado'}</span></span>
      <span className="deal-step">{firstStep ? '↳ ' + firstStep.description + ' · ' + date(firstStep.expectedDate) : isOpen(item) ? '↳ Sem próximo passo' : money.format(item.estimatedCost)}</span>
    </button>;
  }
  return <div className="stack page-content">
    <SummaryCards items={summaries} />
    <div className="toolbar">
      <div className="view-switch" role="group" aria-label="Visualização do funil">
        <button aria-pressed={view === 'board'} onClick={() => setView('board')}>Quadro</button>
        <button aria-pressed={view === 'list'} onClick={() => setView('list')}>Lista</button>
      </div>
      <div className="board-filters">
        <input type="search" aria-label="Buscar empresa ou contato" placeholder="Buscar empresa ou contato" value={search} onChange={event => setSearch(event.target.value)} />
        <select aria-label="Filtrar por etapa" value={stageFilter} onChange={event => setStageFilter(event.target.value)}>
          <option value="">Todas as etapas</option>{stages.map(([key, caption]) => <option key={key} value={key}>{caption}</option>)}
        </select>
        <label className="status-filter"><span>Status das oportunidades</span>
          <select aria-label="Status das oportunidades" value={filter} onChange={event => setFilter(event.target.value)}>
            {statuses.map(([key, caption]) => <option value={key} key={key}>{caption}</option>)}<option value="">Todos os status</option>
          </select>
        </label>
      </div>
    </div>
    {view === 'board' ? <div className="pipeline">{stages.filter(([stage]) => !stageFilter || stage === stageFilter).map(([stage, caption]) => {
      const items = shown.filter(item => item.stage === stage);
      return <section className="lane" key={stage}><h2>{caption} <span>{items.length}</span></h2>
        {items.map(card)}{!items.length && <p className="lane-empty">Nenhuma oportunidade</p>}
      </section>;
    })}</div> : <section className="panel">
      <Table columns={['Oportunidade', 'Contato', 'Etapa', 'Responsável', 'Valor estimado', 'Status']} rows={shown.map(item => {
        const info = opportunityInfo(data, item);
        return [item.id, <button className="text-link" onClick={() => onOpen(item.id)}>{info.title}</button>,
          info.contact, stageNames[item.stage], info.responsible, money.format(item.estimatedCost), statusNames[item.statusNegociacao]];
      })} />
    </section>}
    <div className="board-bottom"><button className="secondary" onClick={() => setCreating(true)}>+ Nova oportunidade</button></div>
    {creating && <Drawer title="Nova oportunidade" onClose={closeCreate}>
      <div className="drawer-header"><p className="eyebrow">FUNIL COMERCIAL</p><h2 id="drawer-title">Nova oportunidade</h2>
        <button className="close-button secondary" aria-label="Fechar" onClick={closeCreate}>×</button></div>
      <div className="drawer-content"><section className="panel">
        {leads.size ? <ActionForm label="Criar oportunidade" action={async values => {
          const created = await save('/negociacao', { ...values, leadId: Number(values.leadId),
            userId: data.user.userId, estimatedCost: Number(values.estimatedCost) });
          setLeadId('');
          closeCreate();
          setFilter('EM_NEGOCIACAO');
          onOpen(created.id);
        }}>
          <Select label="Lead" name="leadId" options={[...leads]} onChange={event => setLeadId(event.target.value)} />
          <Select label="Origem" name="origin" key={leadId} options={origins} defaultValue={origin} />
          <Field label="Valor estimado em reais" name="estimatedCost" type="number" min="0" step="0.01" />
        </ActionForm> : <p>Cadastre um lead para abrir uma oportunidade.</p>}
      </section></div>
    </Drawer>}
  </div>;
}

export function Oportunidade({ item, data, save, onBack, stalledDays = 7 }) {
  const path = '/negociacao/' + item.id;
  const canEdit = isOpen(item) && (data.user.profile === 'GESTOR' || data.user.userId === item.userId);
  const context = { negociacaoId: item.id, userId: data.user.userId };
  const { lead, company, title } = opportunityInfo(data, item);
  const history = [...(item.atividadeDtoList || [])].sort((a, b) => (b.happenDate || '').localeCompare(a.happenDate || '') || b.id - a.id);
  const steps = [...(item.proximoPassoDtoList || [])].sort((a, b) => (a.expectedDate || '').localeCompare(b.expectedDate || ''));
  return <Drawer title={title} onClose={onBack}>
    <div className="drawer-header">
      <p className="muted">{originNames[item.origin] || 'Desconhecida'} · {item.userName} · {stageNames[item.stage]}</p>
      <h2 id="drawer-title">{title}</h2><p>{lead?.name}{company?.cnpj && ' · CNPJ ' + company.cnpj}</p>
      <button className="close-button secondary" aria-label="Fechar" onClick={onBack}>×</button>
    </div>
    <div className="drawer-content">
    <section className="panel detail-overview">
      <h3>Etapa e decisão</h3>
      <p>{stageNames[item.stage]} · {statusNames[item.statusNegociacao]} · {money.format(item.estimatedCost)}</p>
      <p>Responsável · {item.userName || "Usuário #" + item.userId}</p>
      <p>Última movimentação · {date(item.lastMovementDate || item.updatedStageDate || item.startDate)}</p>
      <p className="muted">Aberta em {date(item.startDate)}{item.finalDate && ' · Encerrada em ' + date(item.finalDate)}</p>
      {item.lossReason && <p><strong>Motivo da perda</strong> {item.lossReason}</p>}
      {alerts(item, stalledDays).map(alert => <p className="warning" key={alert}>{alert}</p>)}
      {canEdit && !hasContact(item) && <div className="first-contact">
        <p className="warning">Primeiro contato ainda não registrado.</p>
        <ActionForm label="Registrar primeiro contato" action={async () => {
          await save('/atividade', { ...context, type: 'CONVERSA', description: 'Primeiro contato realizado.', happenDate: today() });
        }} />
      </div>}
      {canEdit && <ActionForm key={item.stage} className="grid-form" label="Mover oportunidade"
        action={values => save(path + '/etapa', values.stage, 'PATCH').then(() => undefined)}>
        <Select label="Nova etapa" name="stage" options={stages.filter(([key]) => key !== item.stage)} />
      </ActionForm>}
    </section>
    <section className="panel detail-history"><h2>Histórico comercial</h2>
      <Table columns={['Data', 'Tipo', 'Descrição', 'Responsável']} rows={history.map(entry =>
        [entry.id, date(entry.happenDate), Object.fromEntries(types)[entry.type], entry.description, entry.userName || "Usuário #" + entry.userId])} />
      {canEdit && <ActionForm className="grid-form" label="Registrar atividade" action={async values => {
        await save('/atividade', { ...values, ...context });
      }}>
        <Select label="Tipo" name="type" options={types} />
        <Field label="Data da atividade" name="happenDate" type="date" defaultValue={today()} max={today()} />
        <Field label="Descrição"><textarea name="description" required maxLength={255} /></Field>
      </ActionForm>}
    </section>
    <section className="panel detail-next"><h2>Próximos passos</h2>
      <Table columns={['Contato', 'Descrição', 'Responsável', 'Status', 'Ações']} rows={steps.map(step => [
        step.id, date(step.expectedDate), step.description, step.userName || "Usuário #" + step.userId,
        pending(step) ? (step.expectedDate < today() ? 'Vencido' : 'Agendado') : 'Realizado',
        canEdit && pending(step) ? <div className="step-actions">
          <ActionForm label="Concluir" action={async () => { await save('/proximopasso/' + step.id + '/status', 'REALIZADA', 'PATCH'); }} />
          <details><summary>Reagendar</summary>
            <ActionForm label="Alterar data" action={async values => {
              await save('/proximopasso/' + step.id + '/data', values.expectedDate, 'PATCH');
            }}><Field label="Nova data" name="expectedDate" type="date" min={today()} /></ActionForm>
          </details>
        </div> : '—',
      ])} />
      {canEdit && <ActionForm className="grid-form" label="Agendar contato" action={async values => {
        await save('/proximopasso', { ...values, ...context, statusProximoPasso: 'AGENDADA' });
      }}>
        <Field label="Próximo passo" name="description" maxLength={255} />
        <Field label="Data do contato" name="expectedDate" type="date" min={today()} />
      </ActionForm>}
    </section>
    <section className="panel detail-diagnosis"><h2>Diagnóstico</h2>
      <Table columns={['Porte', 'Situação', 'Dificuldade', 'Necessidades']} rows={(item.diagnosticoDtoList || []).map(entry =>
        [entry.id, entry.companySize, entry.currentSituation, entry.mainDifficulty, entry.needs])} />
      {canEdit && <ActionForm className="grid-form" label="Registrar diagnóstico" action={async values => {
        await save('/diagnostico', { ...values, ...context, companySize: Number(values.companySize) });
      }}>
        <Field label="Número de funcionários" name="companySize" type="number" min="0" />
        <Field label="Situação atual" name="currentSituation" maxLength={255} />
        <Field label="Dificuldade principal" name="mainDifficulty" maxLength={255} />
        <Field label="Necessidades" name="needs" maxLength={255} />
      </ActionForm>}
    </section>
    {canEdit && <section className="panel detail-close"><h2>Encerrar oportunidade</h2>
      <div className="split">
        <details><summary>Marcar como ganha</summary>
          <ActionForm label="Confirmar venda" action={async () => { await save(path + '/finalizar/ganha', undefined, 'PATCH'); }} />
        </details>
        <details><summary>Marcar como perdida</summary>
          <ActionForm label="Confirmar perda" action={async ({ lossReason, lossDetail }) => {
            if (lossReason === 'Outro' && !lossDetail.trim()) throw new Error('Descreva o outro motivo da perda.');
            if (!lossReason.trim()) throw new Error('Informe o motivo da perda.');
            await save(path + '/finalizar/perda', { lossReason: lossReason === 'Outro' ? 'Outro · ' + lossDetail.trim() : lossReason }, 'PATCH');
          }}>
            <Select label="Motivo da perda" name="lossReason" options={lossReasons.map(reason => [reason, reason])} />
            <Field label="Detalhe para outro motivo" name="lossDetail" maxLength={200} required={false} />
          </ActionForm>
        </details>
      </div>
    </section>}
    </div>
  </Drawer>;
}
