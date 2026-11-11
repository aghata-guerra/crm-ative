import { fireEvent, render, screen } from '@testing-library/react';
import { Funil } from './oportunidades';
import { Agenda } from './agenda';

const data = {
  user: { userId: 1, name: 'Ana', profile: 'VENDEDOR' },
  empresas: [{ id: 1, tradeName: 'Maré Logística' }, { id: 2, tradeName: 'Clínica Aurora' }],
  leads: [{ id: 10, name: 'Fernanda Lima', empresaId: 1 }, { id: 11, name: 'Paulo Mendes', empresaId: 2 }],
  oportunidades: [
    { id: 1, leadId: 10, userId: 1, userName: 'Ana', stage: 'NOVO_LEAD', origin: 'GOOGLE',
      statusNegociacao: 'EM_NEGOCIACAO', estimatedCost: 18000, proximoPassoDtoList: [] },
    { id: 2, leadId: 11, userId: 1, userName: 'Ana', stage: 'QUALIFICACAO', origin: 'INDICACAO',
      statusNegociacao: 'EM_NEGOCIACAO', estimatedCost: 12000, proximoPassoDtoList: [
        { id: 5, description: 'Conversa anterior', expectedDate: '2025-01-01', statusProximoPasso: 'REALIZADA' },
      ] },
  ],
};

test('busca no funil filtra por contato e mantém o resultado ao alternar para lista', () => {
  const onOpen = jest.fn();
  render(<Funil data={data} onOpen={onOpen} />);
  fireEvent.change(screen.getByLabelText('Buscar empresa ou contato'), { target: { value: 'Fernanda' } });
  expect(screen.queryByRole('button', { name: /Clínica Aurora/ })).toBeNull();
  fireEvent.click(screen.getByRole('button', { name: 'Lista', exact: true }));
  fireEvent.click(screen.getByRole('button', { name: 'Maré Logística', exact: true }));
  expect(onOpen).toHaveBeenCalledWith(1);
});

test('agenda identifica ausência de ação pendente e permite consultar ações concluídas', () => {
  const onOpen = jest.fn();
  render(<Agenda data={data} onOpen={onOpen} stalledDays={7} setStalledDays={() => {}} />);
  expect(screen.getAllByText('Sem ação', { selector: 'span' })).toHaveLength(2);
  fireEvent.change(screen.getByLabelText('Situação'), { target: { value: 'REALIZADA' } });
  expect(screen.getByText('Conversa anterior')).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button', { name: 'Clínica Aurora Qualificação' }));
  expect(onOpen).toHaveBeenCalledWith(2);
});
