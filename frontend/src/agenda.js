import { useState } from 'react';
import { openDeal, today, date } from './indicadores';
import { SummaryCards, hasContact, opportunityInfo, pendingSteps, stageNames } from './comercial';
import { Table } from './components';

export function Agenda({ data, onOpen, stalledDays, setStalledDays }) {
  const [filter, setFilter] = useState('');
  const opened = data.oportunidades.filter(openDeal);
  const now = today();
  const rows = data.oportunidades.flatMap(item => {
    const info = opportunityInfo(data, item);
    const steps = item.proximoPassoDtoList || [];
    if (openDeal(item) && !pendingSteps(item).length) {
      return [{ id: 'missing-' + item.id, item, info, state: 'SEM_ACAO', label: 'Sem ação', description: 'Não definida', date: '' },
        ...steps.map(step => ({ id: 'step-' + step.id, item, info, state: 'REALIZADA', label: 'Realizada', description: step.description, date: step.expectedDate || '' }))];
    }
    return steps.map(step => {
      const state = step.statusProximoPasso === 'REALIZADA' ? 'REALIZADA'
        : step.expectedDate < now ? 'VENCIDA' : 'AGENDADA';
      return { id: 'step-' + step.id, item, info, state,
        label: { REALIZADA: 'Realizada', VENCIDA: 'Vencida', AGENDADA: 'Agendada' }[state],
        description: step.description, date: step.expectedDate || '' };
    });
  }).sort((a, b) => {
    const priority = { SEM_ACAO: 0, VENCIDA: 1, AGENDADA: 2, REALIZADA: 3 };
    return priority[a.state] - priority[b.state] || a.date.localeCompare(b.date);
  });
  const activeRows = rows.filter(row => openDeal(row.item));
  const summaries = [
    { label: 'Sem próximo passo', value: activeRows.filter(row => row.state === 'SEM_ACAO').length, note: 'defina uma ação e uma data', attention: true },
    { label: 'Prazo vencido', value: activeRows.filter(row => row.state === 'VENCIDA').length, note: 'precisam de retorno', attention: true },
    { label: 'Para hoje', value: activeRows.filter(row => row.state === 'AGENDADA' && row.date === now).length, note: 'ações previstas' },
    { label: 'Sem primeiro contato', value: opened.filter(item => !hasContact(item)).length, note: 'aguardando atendimento', attention: true },
  ];
  const shown = rows.filter(row => filter ? row.state === filter : openDeal(row.item) && row.state !== 'REALIZADA');
  return <div className="stack page-content">
    <SummaryCards items={summaries} />
    <div className="toolbar agenda-toolbar">
      <label>Situação<select value={filter} onChange={event => setFilter(event.target.value)}>
        <option value="">Pendências em aberto</option><option value="SEM_ACAO">Sem ação</option>
        <option value="VENCIDA">Vencidas</option><option value="AGENDADA">Agendadas</option>
        <option value="REALIZADA">Realizadas</option>
      </select></label>
      <label>Alerta de inatividade em dias<input type="number" min="1" max="365" value={stalledDays}
        onChange={event => {
          const value = Number(event.target.value);
          if (Number.isInteger(value) && value >= 1 && value <= 365) setStalledDays(value);
        }} /></label>
    </div>
    <section className="panel agenda-table">
      <Table columns={['Oportunidade', 'Próxima ação', 'Data', 'Responsável', 'Situação']} rows={shown.map(row => [
        row.id,
        <button className="text-link company-link" onClick={() => onOpen(row.item.id)}>
          <strong>{row.info.title}</strong><small>{stageNames[row.item.stage]}</small>
        </button>,
        row.description, date(row.date), row.info.responsible,
        <span className={'badge ' + (['VENCIDA', 'SEM_ACAO'].includes(row.state) ? 'badge-warning' : 'badge-success')}>{row.label}</span>,
      ])} />
    </section>
  </div>;
}
