import { useState } from 'react';
import { origins, originNames } from './origens';
import { ImportarLeads } from './importacao';
import { ActionForm, Field, Select, Table } from './components';

export function Empresas({ empresas, save }) {
  return <div className="stack">
    <section className="panel">
      <h2>Nova empresa</h2>
      <ActionForm className="grid-form" action={async values => {
        const { telefone, ...body } = values;
        if (!/^[0-9./-]+$/.test(body.cnpj) || body.cnpj.replace(/\D/g, '').length !== 14) {
          throw new Error('Informe um CNPJ com 14 dígitos.');
        }
        const digits = telefone.replace(/\D/g, '');
        await save('/empresa/create', {
          ...body,
          cnpj: body.cnpj.replace(/\D/g, ''),
          telefones: digits ? [{ ddd: digits.slice(0, 2), numero: digits.slice(2) }] : [],
        });
      }}>
        <Field label="Razão social" name="razao_social" maxLength={255} />
        <Field label="Nome fantasia" name="nome_fantasia" maxLength={255} />
        <Field label="CNPJ" name="cnpj" inputMode="numeric" maxLength={18} title="Informe 14 dígitos, com ou sem máscara." />
        <Field label="E-mail" name="email" type="email" />
        <Field label="Telefone com DDD" name="telefone" type="tel" pattern="[0-9]{10,11}" required={false} />
      </ActionForm>
    </section>
    <section className="panel"><h2>Empresas cadastradas</h2>
      <Table columns={['Empresa', 'CNPJ', 'E-mail', 'Telefone']} rows={empresas.map(item =>
        [item.id, item.tradeName || item.companyName, item.cnpj, item.email, item.telephone])} />
    </section>
  </div>;
}

export function Leads({ empresas, leads, save, refresh }) {
  const [search, setSearch] = useState('');
  const companies = new Map(empresas.map(item => [item.id, item.tradeName || item.companyName]));
  const matching = leads.filter(item =>
    [item.name, item.email, item.telephone, companies.get(item.empresaId)]
      .some(value => value?.toLowerCase().includes(search.trim().toLowerCase())));
  return <div className="stack">
    <section className="panel">
      <h2>Novo lead</h2>
      {empresas.length ? <ActionForm className="grid-form" action={async values => {
        await save('/lead', { ...values, email: values.email.trim().toLowerCase(), empresaId: Number(values.empresaId) });
      }}>
        <Field label="Nome" name="name" maxLength={100} />
        <Field label="E-mail" name="email" type="email" />
        <Field label="Telefone" name="telephone" type="tel" pattern="[0-9]{10,12}" />
        <Field label="Cargo" name="position" maxLength={255} />
        <Select label="Empresa" name="empresaId" options={[...companies]} />
        <Select label="Origem" name="origin" options={origins} defaultValue="DESCONHECIDA" />
      </ActionForm> : <p>Cadastre uma empresa na aba Empresas para vincular o primeiro lead.</p>}
    </section>
    {empresas.length > 0 && <ImportarLeads empresas={empresas} save={save} refresh={refresh} />}
    <section className="panel"><h2>Leads cadastrados</h2>
      <Field label="Buscar leads" type="search" value={search} required={false} onChange={event => setSearch(event.target.value)} />
      <Table columns={['Nome', 'Empresa', 'E-mail', 'Telefone', 'Cargo', 'Origem']} rows={matching.map(item =>
        [item.id, item.name, companies.get(item.empresaId), item.email, item.telephone, item.position, originNames[item.origin] || originNames.DESCONHECIDA])} />
    </section>
  </div>;
}
