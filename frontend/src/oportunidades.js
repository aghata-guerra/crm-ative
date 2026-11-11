import { useState } from 'react';
import { openDeal, alerts, date, today } from './indicadores';
import { origins } from './origens';
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
  const leads = new Map(data.leads.map(item => [item.id, item.name]));
  const [leadId, setLeadId] = useState('');
  const origin = data.leads.find(item => item.id === Number(leadId))?.origin || 'DESCONHECIDA';
  return <div className="stack">
    <section className="panel"><h2>Nova oportunidade</h2>
      {leads.size ? <ActionForm className="grid-form" action={async values => {
        const created = await save('/negociacao', { ...values, leadId: Number(values.leadId),
          userId: data.user.userId, estimatedCost: Number(values.estimatedCost) });
        setLeadId('');
        setFilter('EM_NEGOCIACAO');
        onOpen(created.id);
      }}>
        <Select label="Lead" name="leadId" options={[...leads]} onChange={event => setLeadId(event.target.value)} />
        <Select label="Origem" name="origin" key={leadId} options={origins} defaultValue={origin} />
        <Field label="Valor estimado em reais" name="estimatedCost" type="number" min="0" max="9999999999999" step="0.01" />
      </ActionForm> : <p>Cadastre um lead para abrir uma oportunidade.</p>}
    </section>
    <label className="filter">Status das oportunidades
      <select value={filter} onChange={event => setFilter(event.target.value)}>
        {statuses.map(([key, caption]) => <option value={key} key={key}>{caption}</option>)}
        <option value="">Todos os status</option>
      </select>
    </label>
    <div className="pipeline">{stages.map(([stage, caption]) => {
      const items = data.oportunidades.filter(item => item.stage === stage && (!filter || filter === item.statusNegociacao));
      return <section className="lane" key={stage}>
        <h2>{caption} <span>{items.length}</span></h2>
        {items.map(item => <button type="button" className="deal" key={item.id} onClick={() => onOpen(item.id)}>
          <strong>{leads.get(item.leadId) || 'Lead #' + item.leadId}</strong>
          <span>Responsável · {item.userName || "Usuário #" + item.userId}</span>
          <span>Última movimentação · {date(item.lastMovementDate || item.updatedStageDate || item.startDate)}</span>
          <span>{money.format(item.estimatedCost)} · {statusNames[item.statusNegociacao]}</span>
          {alerts(item, stalledDays).map(alert => <span key={alert} className="warning">{alert}</span>)}
        </button>)}
        {!items.length && <p className="muted">Nenhuma oportunidade</p>}
      </section>;
    })}</div>
  </div>;
}

export function Oportunidade({ item, data, save, onBack, stalledDays = 7 }) {
  const path = '/negociacao/' + item.id;
  const canEdit = isOpen(item) && (data.user.profile === 'GESTOR' || data.user.userId === item.userId);
  const context = { negociacaoId: item.id, userId: data.user.userId };
  const lead = data.leads.find(candidate => candidate.id === item.leadId);
  const history = [...(item.atividadeDtoList || [])].sort((a, b) => (b.happenDate || '').localeCompare(a.happenDate || '') || b.id - a.id);
  const steps = [...(item.proximoPassoDtoList || [])].sort((a, b) => (a.expectedDate || '').localeCompare(b.expectedDate || ''));
  return <div className="stack">
    <div><button className="secondary" onClick={onBack}>Voltar ao funil</button></div>
    <section className="panel">
      <h2>{lead?.name || 'Lead #' + item.leadId}</h2>
      <p>{stageNames[item.stage]} · {statusNames[item.statusNegociacao]} · {money.format(item.estimatedCost)}</p>
      <p>Responsável · {item.userName || "Usuário #" + item.userId}</p>
      <p>Última movimentação · {date(item.lastMovementDate || item.updatedStageDate || item.startDate)}</p>
      <p className="muted">Aberta em {date(item.startDate)}{item.finalDate && ' · Encerrada em ' + date(item.finalDate)}</p>
      {item.lossReason && <p><strong>Motivo da perda</strong> {item.lossReason}</p>}
      {alerts(item, stalledDays).map(alert => <p className="warning" key={alert}>{alert}</p>)}
      {canEdit && <ActionForm key={item.stage} className="grid-form" label="Mover oportunidade"
        action={values => save(path + '/etapa', values.stage, 'PATCH').then(() => undefined)}>
        <Select label="Nova etapa" name="stage" options={stages.filter(([key]) => key !== item.stage)} />
      </ActionForm>}
    </section>
    <section className="panel"><h2>Histórico comercial</h2>
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
    <section className="panel"><h2>Próximos passos</h2>
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
    <section className="panel"><h2>Diagnóstico</h2>
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
    {canEdit && <section className="panel"><h2>Encerrar oportunidade</h2>
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
  </div>;
}
