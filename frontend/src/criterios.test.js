import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { Indicadores } from './indicadores';
import { Leads } from './cadastros';
import { ImportarLeads } from './importacao';

const empresas = [{ id: 1, cnpj: '11222333000181', tradeName: 'Empresa Teste' }];
const leads = [{ id: 1, name: 'Ana Silva', email: 'ana@example.com', telephone: '81988887777',
  position: 'Gerente', empresaId: 1, origin: 'INDICACAO' }];

test('relatório aplica período, mostra amostra e permite conferir as vendas', () => {
  const opportunities = [
    { id: 1, leadId: 1, userName: 'Vendedor', stage: 'PROPOSTA', statusNegociacao: 'ENCERRADA_GANHA',
      startDate: '2025-01-01', finalDate: '2025-01-10' },
    { id: 2, leadId: 1, stage: 'DIAGNOSTICO', statusNegociacao: 'ENCERRADA_PERDA',
      startDate: '2025-02-01', finalDate: '2025-02-20' },
    { id: 3, leadId: 1, stage: 'PROPOSTA', statusNegociacao: 'ENCERRADA_GANHA',
      startDate: '2025-02-20', finalDate: '2025-02-01' },
  ];
  const onOpen = jest.fn();
  render(<Indicadores data={{ empresas, leads, oportunidades: opportunities, user: { profile: 'GESTOR' } }}
    onOpen={onOpen} stalledDays={7} setStalledDays={() => {}} />);
  expect(screen.getByText('14 dias')).toBeInTheDocument();
  expect(screen.getByText(/1 registro.*datas ausentes ou inválidas/)).toBeInTheDocument();
  fireEvent.change(screen.getByLabelText('Fechamentos a partir de'), { target: { value: '2025-01-01' } });
  fireEvent.change(screen.getByLabelText('Fechamentos até'), { target: { value: '2025-01-31' } });
  expect(screen.getByText('9 dias')).toBeInTheDocument();
  const originRow = screen.getByRole('cell', { name: 'Indicação' }).closest('tr');
  fireEvent.click(within(originRow).getByRole('button', { name: 'Conferir vendas' }));
  fireEvent.click(screen.getByRole('button', { name: 'Abrir oportunidade' }));
  expect(onOpen).toHaveBeenCalledWith(1);
});

test('conflito de cadastro apresenta o contato existente', async () => {
  const error = Object.assign(new Error('Contato duplicado'), { status: 409, details: { LeadId: 1 } });
  render(<Leads empresas={empresas} leads={leads} save={jest.fn().mockRejectedValue(error)} />);
  fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'Ana Silva' } });
  fireEvent.change(screen.getByLabelText('E-mail'), { target: { value: 'ana@example.com' } });
  fireEvent.change(screen.getByLabelText('Telefone'), { target: { value: '81988887777' } });
  fireEvent.change(screen.getByLabelText('Cargo'), { target: { value: 'Gerente' } });
  fireEvent.change(screen.getByLabelText('Empresa'), { target: { value: '1' } });
  fireEvent.submit(screen.getByRole('button', { name: 'Salvar' }).closest('form'));
  expect(await screen.findByRole('heading', { name: 'Contato já cadastrado' })).toBeInTheDocument();
});

test('importação identifica os contatos salvos e as linhas que falharam', async () => {
  const save = jest.fn().mockResolvedValueOnce({ id: 2 }).mockRejectedValueOnce(new Error('E-mail duplicado'));
  render(<ImportarLeads empresas={empresas} save={save} refresh={() => {}} />);
  const file = new File([''], 'leads.csv', { type: 'text/csv' });
  const oldText = File.prototype.text;
  File.prototype.text = async () => 'nome;email;telefone;cargo;cnpj\nAna Silva;ana@example.com;81988887777;Gerente;11222333000181\nBruno Lima;bruno@example.com;81977776666;Gerente;11222333000181';
  fireEvent.change(screen.getByLabelText('Arquivo CSV'), { target: { files: [file] } });
  fireEvent.submit(screen.getByRole('button', { name: 'Importar contatos', hidden: true }).closest('form'));
  await waitFor(() => expect(save).toHaveBeenCalledTimes(2));
  expect(await screen.findByText(/Registro 1 · Ana Silva · ana@example.com/)).toBeInTheDocument();
  expect(screen.getByText(/Registro 2 · E-mail duplicado/)).toBeInTheDocument();
  File.prototype.text = oldText;
});
