import { daysBetween, today, validDate } from './dates';

export const stages = [
  ['NOVO_LEAD', 'Novo lead'], ['QUALIFICACAO', 'Qualificação'],
  ['DIAGNOSTICO', 'Diagnóstico'], ['PROPOSTA', 'Proposta'],
];
export const stageNames = Object.fromEntries(stages);
export const statusNames = {
  EM_NEGOCIACAO: 'Em negociação', ENCERRADA_GANHA: 'Ganha', ENCERRADA_PERDA: 'Perdida',
};
export const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
export const pendingSteps = item => (item.proximoPassoDtoList || [])
  .filter(step => step.statusProximoPasso !== 'REALIZADA')
  .sort((a, b) => (a.expectedDate || '').localeCompare(b.expectedDate || ''));
export const hasContact = item => (item.atividadeDtoList || [])
  .some(activity => ['CONVERSA', 'REUNIAO', 'PROPOSTA'].includes(activity.type));

export function daysIdle(item) {
  const value = item.lastMovementDate || item.updatedStageDate || item.startDate;
  return validDate(value) ? Math.max(0, daysBetween(value, today())) : null;
}

export function opportunityInfo(data, item) {
  const lead = data.leads.find(candidate => candidate.id === item.leadId);
  const company = (data.empresas || []).find(candidate => candidate.id === lead?.empresaId);
  return {
    lead,
    company,
    title: company?.tradeName || company?.companyName || lead?.name || 'Lead #' + item.leadId,
    contact: lead?.name || 'Lead #' + item.leadId,
    responsible: item.userName || 'Usuário #' + item.userId,
  };
}

export function SummaryCards({ items }) {
  return <div className="summary-cards">{items.map(({ label, value, note, attention }) =>
    <section className="summary-card" key={label}>
      <span>{label}</span>
      <strong className={attention ? 'attention-number' : ''}>{value}</strong>
      <small>{note}</small>
    </section>)}</div>;
}

export function BarChart({ items, onSelect, label }) {
  const largest = Math.max(1, ...items.map(item => item.value));
  return <div className={'bar-chart ' + (label === 'perdas' ? 'losses' : '')}>
    {items.map(item => <button className="bar-row" key={item.key}
      aria-label={'Conferir ' + label + ' de ' + item.caption}
      onClick={() => onSelect(item)}>
      <span>{item.caption}</span><span className="bar-track" aria-hidden="true">
        <span className="bar-fill" style={{ width: (100 * item.value / largest) + '%' }} />
      </span><strong>{item.value}</strong>
    </button>)}
  </div>;
}
