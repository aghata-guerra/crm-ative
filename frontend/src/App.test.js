import { fireEvent, render, screen } from '@testing-library/react';
import App from './App';
import { Funil } from './oportunidades';

beforeEach(() => sessionStorage.clear());

test('oferece login e cadastro de conta', () => {
  render(<App />);
  expect(screen.getByRole('heading', { name: 'Entrar no CRM' })).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button', { name: 'Criar uma conta' }));
  expect(screen.getByLabelText('Nome completo')).toBeRequired();
});

test('mostra oportunidades abertas e permite localizar uma perda pelo filtro', () => {
  const open = { id: 1, leadId: 10, stage: 'NOVO_LEAD', statusNegociacao: 'EM_NEGOCIACAO', estimatedCost: 120 };
  const closed = { ...open, id: 2, leadId: 11, statusNegociacao: 'ENCERRADA_PERDA' };
  const onOpen = jest.fn();
  render(<Funil data={{ leads: [{ id: 10, name: 'Ana Silva' }, { id: 11, name: 'Bruno Lima' }],
    oportunidades: [open, closed] }} onOpen={onOpen} />);
  expect(screen.getByRole('button', { name: /Ana Silva.*Sem próximo passo/ })).toBeInTheDocument();
  expect(screen.queryByRole('button', { name: /Bruno Lima/ })).toBeNull();
  fireEvent.change(screen.getByLabelText('Status das oportunidades'), { target: { value: 'ENCERRADA_PERDA' } });
  fireEvent.click(screen.getByRole('button', { name: /Bruno Lima/ }));
  expect(onOpen).toHaveBeenCalledWith(2);
});
