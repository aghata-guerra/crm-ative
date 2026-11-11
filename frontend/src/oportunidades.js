import { useState } from 'react';
import { origins } from './origens';
import { ActionForm, Field, Select, Table } from './components';

const stages = [
  ['NOVO_LEAD', 'Novo lead'], ['QUALIFICACAO', 'Qualificação'],
  ['DIAGNOSTICO', 'Diagnóstico'], ['PROPOSTA', 'Proposta'],
];
const statuses = [
  ['EM_NEGOCIACAO', 'Em negociação'], ['ENCERRADA_GANHA', 'Ganha'], ['ENCERRADA_PERDA', 'Perdida'],
];
const types = [['CONVERSA', 'Conversa'], ['REUNIAO', 'Reunião'], ['PROPOSTA', 'Proposta'], ['OBSERVACAO', 'Observação']];
const stageNames = Object.fromEntries(stages);
const statusNames = Object.fromEntries(statuses);
const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const stalledDays = 7;

function today() {
  return new Intl.DateTimeFormat('sv-SE').format(new Date());
}
function date(value) {
  return value ? value.split('-').reverse().join('/') : '—';
}
function daysBetween(start, end) {
  return Math.round((Date.parse(end) - Date.parse(start)) / 86400000);
}
const isOpen = item => item.statusNegociacao === 'EM_NEGOCIACAO';
const pending = step => step.statusProximoPasso !== 'REALIZADA';

function alerts(item) {
  if (!isOpen(item)) return [];
  const steps = (item.proximoPassoDtoList || []).filter(pending);
  const now = today();
  return [
    !steps.length && 'Sem próximo passo',
    steps.some(step => step.expectedDate && step.expectedDate < now) && 'Contato atrasado',
    steps.some(step => step.expectedDate === now) && 'Contato hoje',
    daysBetween(item.updatedStageDate || item.startDate, now) >= stalledDays && 'Parada há 7 dias ou mais',
  ].filter(Boolean);
}

export function Indicadores({ data }) {
  const { empresas, leads, oportunidades, user } = data;
  const wins = oportunidades.filter(item => item.statusNegociacao === 'ENCERRADA_GANHA');
  const losses = oportunidades.filter(item => item.statusNegociacao === 'ENCERRADA_PERDA');
  const closed = [...wins, ...losses].filter(item => item.startDate && item.finalDate);
  const average = closed.length ? closed.reduce((sum, item) =>
    sum + daysBetween(item.startDate, item.finalDate), 0) / closed.length : null;
  const leadNames = new Map(leads.map(item => [item.id, item.name]));
  const metrics = [
    ['Empresas', empresas.length], ['Leads', leads.length],
    ['Em negociação', oportunidades.filter(isOpen).length], ['Vendas ganhas', wins.length],
    ['Tempo médio até fechar', average === null ? '—' : average.toLocaleString('pt-BR', { maximumFractionDigits: 1 }) + ' dias'],
  ];
  return <div className="stack">
    <p className="muted">{user.profile === 'GESTOR' ? 'Oportunidades da equipe' : 'Minhas oportunidades'}</p>
    <div className="metrics">{metrics.map(([title, number]) => <div className="panel" key={title}>
      <span>{title}</span><strong>{number}</strong>
    </div>)}</div>
    <section className="panel"><h2>Alertas de acompanhamento</h2>
      <Table columns={['Lead', 'Alerta']} rows={oportunidades.flatMap(item => {
        const messages = alerts(item);
        return messages.length ? [[item.id, leadNames.get(item.leadId) || 'Lead #' + item.leadId, messages.join(' · ')]] : [];
      })} />
    </section>
    <div className="split">
      <section className="panel"><h2>Vendas ganhas por origem</h2>
        <Table columns={['Origem', 'Vendas ganhas']} rows={origins.map(([key, caption]) =>
          [key, caption, wins.filter(item => item.origin === key).length])} />
      </section>
      <section className="panel"><h2>Perdas por etapa</h2>
        <Table columns={['Etapa de encerramento', 'Perdas']} rows={stages.map(([key, caption]) =>
          [key, caption, losses.filter(item => item.stage === key).length])} />
      </section>
    </div>
  </div>;
}

export function Funil({ data, save, onOpen }) {
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
          <span>{money.format(item.estimatedCost)} · {statusNames[item.statusNegociacao]}</span>
          {alerts(item).map(alert => <span key={alert} className="warning">{alert}</span>)}
        </button>)}
        {!items.length && <p className="muted">Nenhuma oportunidade</p>}
      </section>;
    })}</div>
  </div>;
}

export function Oportunidade({ item, data, save, onBack }) {
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
      <p className="muted">Aberta em {date(item.startDate)}{item.finalDate && ' · Encerrada em ' + date(item.finalDate)}</p>
      {item.lossReason && <p><strong>Motivo da perda</strong> {item.lossReason}</p>}
      {alerts(item).map(alert => <p className="warning" key={alert}>{alert}</p>)}
      {canEdit && <ActionForm key={item.stage} className="grid-form" label="Mover oportunidade"
        action={values => save(path + '/etapa', values.stage, 'PATCH').then(() => undefined)}>
        <Select label="Nova etapa" name="stage" options={stages.filter(([key]) => key !== item.stage)} />
      </ActionForm>}
    </section>
    <section className="panel"><h2>Histórico comercial</h2>
      <Table columns={['Data', 'Tipo', 'Descrição']} rows={history.map(entry =>
        [entry.id, date(entry.happenDate), Object.fromEntries(types)[entry.type], entry.description])} />
      {canEdit && <ActionForm className="grid-form" label="Registrar atividade" action={async values => {
        await save('/atividade', { ...values, ...context });
      }}>
        <Select label="Tipo" name="type" options={types} />
        <Field label="Data da atividade" name="happenDate" type="date" defaultValue={today()} max={today()} />
        <Field label="Descrição"><textarea name="description" required maxLength={255} /></Field>
      </ActionForm>}
    </section>
    <section className="panel"><h2>Próximos passos</h2>
      <Table columns={['Contato', 'Descrição', 'Status', 'Ações']} rows={steps.map(step => [
        step.id, date(step.expectedDate), step.description,
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
          <ActionForm label="Confirmar perda" action={async ({ lossReason }) => {
            if (!lossReason.trim()) throw new Error('Informe o motivo da perda.');
            await save(path + '/finalizar/perda', { lossReason: lossReason.trim() }, 'PATCH');
          }}>
            <Field label="Motivo da perda"><textarea name="lossReason" maxLength={255} required /></Field>
          </ActionForm>
        </details>
      </div>
    </section>}
  </div>;
}
