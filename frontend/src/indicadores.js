import { useState } from 'react';
import { Field, Table } from './components';
import { origins } from './origens';

const stages = [
  ['NOVO_LEAD', 'Novo lead'], ['QUALIFICACAO', 'Qualificação'],
  ['DIAGNOSTICO', 'Diagnóstico'], ['PROPOSTA', 'Proposta'],
];
const stageNames = Object.fromEntries(stages);
export const openDeal = item => item.statusNegociacao === 'EM_NEGOCIACAO';

export function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value || '')) return false;
  const parsed = new Date(value + 'T00:00:00Z');
  return Number.isFinite(parsed.getTime()) && parsed.toISOString().slice(0, 10) === value;
}
export function today() {
  return new Intl.DateTimeFormat('sv-SE').format(new Date());
}
export function date(value) {
  return validDate(value) ? value.split('-').reverse().join('/') : '—';
}
export function daysBetween(start, end) {
  return Math.round((Date.parse(end) - Date.parse(start)) / 86400000);
}
export function alerts(item, stalledDays = 7) {
  if (!openDeal(item)) return [];
  const steps = (item.proximoPassoDtoList || []).filter(step => step.statusProximoPasso !== 'REALIZADA');
  const now = today();
  const movement = item.lastMovementDate || item.updatedStageDate || item.startDate;
  return [
    !steps.length && 'Sem próximo passo',
    steps.some(step => step.expectedDate && step.expectedDate < now) && 'Contato atrasado',
    steps.some(step => step.expectedDate === now) && 'Contato hoje',
    validDate(movement) && daysBetween(movement, now) >= stalledDays && 'Sem movimentação há ' + stalledDays + ' dias ou mais',
  ].filter(Boolean);
}

export function Indicadores({ data, onOpen, stalledDays, setStalledDays }) {
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [selection, setSelection] = useState(null);
  const { empresas, leads, oportunidades, user } = data;
  const names = new Map(leads.map(item => [item.id, item.name]));
  const leadOrigins = new Map(leads.map(item => [item.id, item.origin]));
  const originOf = item => leadOrigins.get(item.leadId) || item.origin || 'DESCONHECIDA';
  const closed = oportunidades.filter(item => !openDeal(item));
  const invalid = closed.filter(item => !validDate(item.startDate) || !validDate(item.finalDate) || item.finalDate < item.startDate || item.finalDate > today());
  const periodInvalid = Boolean(start && end && start > end);
  const inPeriod = item => validDate(item.finalDate)
    && (!start || item.finalDate >= start) && (!end || item.finalDate <= end);
  const sample = periodInvalid ? [] : closed.filter(inPeriod);
  const wins = sample.filter(item => item.statusNegociacao === 'ENCERRADA_GANHA');
  const losses = sample.filter(item => item.statusNegociacao === 'ENCERRADA_PERDA');
  const durations = sample.filter(item => !invalid.includes(item));
  const average = durations.length ? durations.reduce((sum, item) =>
    sum + daysBetween(item.startDate, item.finalDate), 0) / durations.length : null;
  const lossCounts = stages.map(([key]) => losses.filter(item => item.stage === key).length);
  const maxLosses = Math.max(...lossCounts);
  const selected = selection?.kind === 'origin' ? wins.filter(item => originOf(item) === selection.key)
    : selection?.kind === 'stage' ? losses.filter(item => item.stage === selection.key) : durations;
  const closeDates = closed.map(item => item.finalDate).filter(validDate).sort();
  const period = start || end
    ? (start ? date(start) : 'Início da base') + ' até ' + (end ? date(end) : 'Fim da base')
    : 'Todo o período registrado' + (closeDates.length ? ' · ' + date(closeDates[0]) + ' até ' + date(closeDates.at(-1)) : '');
  const metrics = [
    ['Empresas', empresas.length], ['Leads', leads.length],
    ['Em negociação', oportunidades.filter(openDeal).length], ['Vendas ganhas no período', wins.length],
    ['Tempo médio até fechar', average === null ? '—' : average.toLocaleString('pt-BR', { maximumFractionDigits: 1 }) + ' dias'],
  ];
  return <div className="stack">
    <section className="panel">
      <h2>Período dos resultados comerciais</h2>
      <div className="split">
        <Field label="Fechamentos a partir de" type="date" value={start} required={false}
          onChange={event => { setStart(event.target.value); setSelection(null); }} />
        <Field label="Fechamentos até" type="date" value={end} required={false}
          onChange={event => { setEnd(event.target.value); setSelection(null); }} />
      </div>
      {periodInvalid && <p role="alert" className="error">A data inicial deve ser anterior ou igual à data final.</p>}
      <p>{period}. {user.profile === 'GESTOR' ? 'Carteiras da equipe.' : 'Sua carteira.'}</p>
      <p className="muted">Base dos resultados · {sample.length} oportunidades encerradas, {wins.length} ganhas e {losses.length} perdidas.
        Média calculada sobre {durations.length} oportunidades com datas válidas.</p>
      {!!invalid.length && <p className="warning">{invalid.length} registro(s) com datas ausentes ou inválidas excluído(s) do cálculo da média.</p>}
    </section>
    <div className="metrics">{metrics.map(([title, number]) => <div className="panel" key={title}>
      <span>{title}</span><strong>{number}</strong>
    </div>)}</div>
    <section className="panel"><h2>Alertas de acompanhamento</h2>
      <Field label="Dias sem movimentação para gerar alerta" type="number" min="1" max="365" value={stalledDays}
        onChange={event => {
          const value = Number(event.target.value);
          if (Number.isInteger(value) && value >= 1 && value <= 365) setStalledDays(value);
        }} />
      <p className="muted">A última movimentação considera mudanças de etapa e atividades registradas.</p>
      <Table columns={['Lead', 'Etapa', 'Responsável', 'Última movimentação', 'Alerta', 'Ação']}
        rows={oportunidades.flatMap(item => {
          const messages = alerts(item, stalledDays);
          return messages.length ? [[item.id, names.get(item.leadId) || 'Lead #' + item.leadId,
            stageNames[item.stage], item.userName || 'Usuário #' + item.userId,
            date(item.lastMovementDate || item.updatedStageDate || item.startDate), messages.join(' · '),
            <button onClick={() => onOpen(item.id)}>Abrir oportunidade</button>]] : [];
        })} />
    </section>
    <div className="split">
      <section className="panel"><h2>Vendas ganhas por origem do lead</h2>
        <p className="muted">Quantidade de vendas ganhas · {wins.length} registros no período.</p>
        <Table columns={['Origem', 'Quantidade', 'Registros']} rows={origins.map(([key, caption]) =>
          [key, caption, wins.filter(item => originOf(item) === key).length,
            <button className="secondary" onClick={() => setSelection({ kind: 'origin', key, title: caption })}>Conferir vendas</button>])} />
      </section>
      <section className="panel"><h2>Perdas por etapa</h2>
        <p className="muted">Quantidade de oportunidades perdidas · {losses.length} registros no período.</p>
        {maxLosses > 0 && <p>Maior quantidade de perdas · {stages.filter((_, index) => lossCounts[index] === maxLosses)
          .map(([, caption]) => caption).join(', ')} ({maxLosses}).</p>}
        <Table columns={['Etapa de encerramento', 'Quantidade', 'Registros']} rows={stages.map(([key, caption], index) =>
          [key, caption, lossCounts[index],
            <button className="secondary" onClick={() => setSelection({ kind: 'stage', key, title: caption })}>Conferir perdas</button>])} />
      </section>
    </div>
    <button className="secondary" onClick={() => setSelection({ kind: 'duration', title: 'Cálculo do tempo médio' })}>Conferir base do tempo médio</button>
    {selection && <section className="panel"><h2>{selection.title}</h2>
      <p>{selected.length} registro(s) · {period}</p>
      <Table columns={['Lead', 'Responsável', 'Entrada', 'Fechamento', 'Dias', 'Ação']} rows={selected.map(item =>
        [item.id, names.get(item.leadId), item.userName, date(item.startDate), date(item.finalDate),
          validDate(item.startDate) && validDate(item.finalDate) && item.finalDate >= item.startDate ? daysBetween(item.startDate, item.finalDate) : 'Datas inválidas',
          <button onClick={() => onOpen(item.id)}>Abrir oportunidade</button>])} />
    </section>}
  </div>;
}
