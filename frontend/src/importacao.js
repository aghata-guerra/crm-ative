import { useState } from 'react';
import { ActionForm, Field } from './components';

function parseCsv(source) {
  const text = source.replace(/^\uFEFF/, '').replace(/\r\n?/g, '\n');
  const separator = text.split('\n', 1)[0].includes(';') ? ';' : ',';
  const rows = [];
  let row = [];
  let field = '';
  let quoted = false;
  for (let index = 0; index <= text.length; index++) {
    const char = text[index];
    if (char === '"' && quoted && text[index + 1] === '"') {
      field += '"';
      index++;
    } else if (char === '"' && (quoted || !field)) {
      quoted = !quoted;
    } else if (!quoted && (char === separator || char === '\n' || char === undefined)) {
      row.push(field.trim());
      field = '';
      if (char !== separator) {
        if (row.some(Boolean)) rows.push(row);
        row = [];
      }
    } else if (char !== undefined) {
      field += char;
    }
  }
  if (quoted) throw new Error('Há aspas sem fechamento no CSV.');
  return rows;
}

export function ImportarLeads({ empresas, save, refresh }) {
  const [errors, setErrors] = useState([]);
  const [successes, setSuccesses] = useState([]);
  return <details className="panel"><summary>Importar leads de um CSV</summary>
    <p>Use as colunas nome, email, telefone, cargo e cnpj. As empresas precisam estar cadastradas.</p>
    <ActionForm label="Importar contatos" action={async ({ arquivo }) => {
      setErrors([]);
      setSuccesses([]);
      if (arquivo.size > 1024 * 1024) throw new Error('O arquivo deve ter até 1 MB.');
      const [header, ...rows] = parseCsv(await arquivo.text());
      const keys = ['nome', 'email', 'telefone', 'cargo', 'cnpj'];
      const positions = keys.map(key => header?.findIndex(value => value.toLowerCase() === key));
      if (positions.some(index => index === undefined || index < 0) || !rows.length) {
        throw new Error('Confira o cabeçalho do CSV e inclua pelo menos um contato.');
      }
      const companies = new Map(empresas.map(company => [company.cnpj.replace(/\D/g, ''), company.id]));
      const rejected = [];
      const imported = [];
      let created = 0;
      for (const [index, row] of rows.entries()) {
        const [name, rawEmail, phone, position, cnpj] = positions.map(column => row[column] || '');
        const email = rawEmail.toLowerCase();
        const telephone = phone.replace(/\D/g, '');
        const empresaId = companies.get(cnpj.replace(/\D/g, ''));
        try {
          if (!empresaId) throw new Error('CNPJ sem empresa cadastrada.');
          if (!name || !position || !/^\S+@\S+\.\S+$/.test(email) || !/^[0-9]{10,12}$/.test(telephone)) {
            throw new Error('Confira nome, e-mail, telefone e cargo.');
          }
          await save('/lead', { name, email, telephone, position, empresaId }, 'POST', false);
          created++;
          imported.push('Registro ' + (index + 1) + ' · ' + name + ' · ' + email);
        } catch (error) {
          rejected.push('Registro ' + (index + 1) + ' · ' + error.message);
          if (error.status === 401) {
            rows.slice(index + 1).forEach((_, offset) => rejected.push('Registro ' + (index + offset + 2) + ' · Não importado porque a sessão expirou.'));
            break;
          }
        }
      }
      if (created) refresh();
      setErrors(rejected);
      setSuccesses(imported);
      return created + ' contato(s) importado(s) de ' + rows.length + '.';
    }}>
      <Field label="Arquivo CSV" name="arquivo" type="file" accept=".csv,text/csv" />
    </ActionForm>
    {!!successes.length && <div className="success"><h3>Contatos importados</h3><ul>{successes.map(item => <li key={item}>{item}</li>)}</ul></div>}
    {!!errors.length && <ul className="error">{errors.map(error => <li key={error}>{error}</li>)}</ul>}
  </details>;
}
