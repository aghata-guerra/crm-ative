import { useState } from 'react';
import { Field, Table } from './components';
import { date, today, validDate, daysBetween } from './dates';

import { BarChart, money, SummaryCards } from './comercial';
import { origins } from './origens';

export { date, today, validDate, daysBetween } from './dates';

const stages = [
  ['NOVO_LEAD', 'Novo lead'], ['QUALIFICACAO', 'Qualificação'],
  ['DIAGNOSTICO', 'Diagnóstico'], ['PROPOSTA', 'Proposta'],
];

export const openDeal = item => item.statusNegociacao === 'EM_NEGOCIACAO';

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

export function Indicadores({ data, onOpen }) {
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [selection, setSelection] = useState(null);
  const { leads, oportunidades, user } = data;
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
    { label: 'Vendas ganhas', value: wins.length, note: money.format(wins.reduce((sum, item) => sum + Number(item.estimatedCost || 0), 0)) + ' em contratos' },
    { label: 'Vendas perdidas', value: losses.length, note: 'motivo registrado' },
    { label: 'Conversão', value: sample.length ? Math.round(100 * wins.length / sample.length) + '%' : '—', note: 'das oportunidades concluídas' },
    { label: 'Tempo médio de venda', value: average === null ? '—' : average.toLocaleString('pt-BR', { maximumFractionDigits: 1 }) + ' dias', note: durations.length + ' oportunidades com datas válidas' },
  ];
  return <div className="stack reports-container">
    <SummaryCards items={metrics} />
    <section className="panel reports-filters">
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
    <div className="split reports-charts">
      <section className="panel"><h2>Vendas ganhas por origem do lead</h2>
        <p className="muted">Quantidade de vendas ganhas · {wins.length} registros no período.</p>
        <BarChart label="vendas" items={origins.map(([key, caption]) => ({
          key, caption, value: wins.filter(item => originOf(item) === key).length,
        }))} onSelect={item => setSelection({ kind: 'origin', key: item.key, title: item.caption })} />
      </section>
      <section className="panel"><h2>Perdas por etapa</h2>
        <p className="muted">Quantidade de oportunidades perdidas · {losses.length} registros no período.</p>
        {maxLosses > 0 && <p>Maior quantidade de perdas · {stages.filter((_, index) => lossCounts[index] === maxLosses)
          .map(([, caption]) => caption).join(', ')} ({maxLosses}).</p>}
        <BarChart label="perdas" items={stages.map(([key, caption], index) => ({
          key, caption, value: lossCounts[index],
        }))} onSelect={item => setSelection({ kind: 'stage', key: item.key, title: item.caption })} />
        {!!losses.length && <p className="chart-footnote">Motivos registrados · {[...new Set(losses.map(item => item.lossReason).filter(Boolean))].join(' · ') || 'Não informados'}</p>}
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
