import { useCallback, useEffect, useState } from 'react';
import { list, request } from './api';
import { ActionForm, ErrorMessage, Field } from './components';
import { Agenda } from './agenda';
import { Empresas, Leads } from './cadastros';
import { Indicadores } from './indicadores';
import { Funil, Oportunidade } from './oportunidades';
import './App.css';

const pages = [
  { key: 'Funil', label: 'Funil comercial', icon: '◫' },
  { key: 'Agenda', label: 'Próximos passos', icon: '▣' },
  { key: 'Indicadores', label: 'Indicadores', icon: '▥' },
  { key: 'Empresas', label: 'Empresas', icon: '▤', group: 'Cadastros' },
  { key: 'Leads', label: 'Leads e contatos', icon: '◉' },
];
const headings = {
  Funil: ['VISÃO COMERCIAL', 'Funil de oportunidades', 'Acompanhe cada negócio, do primeiro contato à decisão.'],
  Agenda: ['AGENDA COMERCIAL', 'Próximos passos', 'Priorize pendências e mantenha cada oportunidade com uma próxima ação.'],
  Indicadores: ['RESULTADOS COMERCIAIS', 'Indicadores comerciais', 'Acompanhe os resultados e confira os registros de cada indicador.'],
  Empresas: ['CADASTROS', 'Empresas', 'Centralize as informações das empresas atendidas pela equipe.'],
  Leads: ['CADASTROS', 'Leads e contatos', 'Organize seus contatos e registre a origem de cada oportunidade.'],
};
const sessionKey = 'ative-crm-token';

function Access({ onLogin, expired }) {
  const [register, setRegister] = useState(false);
  const [notice, setNotice] = useState('');
  async function submit(body) {
    body.email = body.email.trim().toLowerCase();
    if (register) {
      body.name = body.name.trim();
      if (!body.name) throw new Error('Informe o nome completo.');
      await request('/auth/register', { method: 'POST', body });
      setRegister(false);
      setNotice('Conta criada. Entre com seu e-mail e senha.');
    } else {
      const result = await request('/auth/login', { method: 'POST', body });
      onLogin(result.acessToken);
    }
  }
  return <main className="access">
    <section className="panel">
      <p className="brand">ative<span>.</span></p>
      <h1>{register ? 'Criar conta' : 'Entrar no CRM'}</h1>
      {(notice || expired) && <p role="status">{notice || expired}</p>}
      <ActionForm key={String(register)} action={submit} label={register ? 'Criar conta' : 'Entrar'}>
        {register && <Field label="Nome completo" name="name" autoComplete="name" maxLength={100} />}
        <Field label="E-mail" name="email" type="email" autoComplete="email" />
        <Field label="Senha" name="password" type="password" autoComplete={register ? 'new-password' : 'current-password'} minLength={register ? 8 : undefined} />
      </ActionForm>
      <button type="button" className="secondary" onClick={() => { setRegister(!register); setNotice(''); }}>
        {register ? 'Voltar para o login' : 'Criar uma conta'}
      </button>
    </section>
  </main>;
}

function Workspace({ api, logout }) {
  const [page, setPage] = useState('Funil');
  const [stalledDays, setStalledDays] = useState(7);
  function openOpportunity(id) {
    setSelected(id);
  }
  const [selected, setSelected] = useState(null);
  const [data, setData] = useState(null);
  const [version, setVersion] = useState(0);
  const [error, setError] = useState('');
  const refresh = () => setVersion(previous => previous + 1);

  useEffect(() => {
    const controller = new AbortController();
    const { signal } = controller;
    async function load() {
      setError('');
      try {
        const user = await api('/auth/me', { signal });
        const [empresas, leads, oportunidades] = await Promise.all([
          list(api, '/empresa', signal),
          list(api, '/lead', signal),
          user.profile === 'GESTOR' ? list(api, '/negociacao', signal) : api('/negociacao/user', { signal }),
        ]);
        if (!signal.aborted) setData({ user, empresas, leads, oportunidades });
      } catch (failure) {
        if (!signal.aborted) setError(failure.message);
      }
    }
    load();
    return () => controller.abort();
  }, [api, version]);

  async function save(path, body, method = 'POST', reload = true) {
    const response = await api(path, { method, body });
    if (reload) refresh();
    return response;
  }

  const opportunity = data?.oportunidades.find(item => item.id === selected);
  const [eyebrow, title, description] = headings[page];
  const initials = (data?.user.name || '').split(/\s+/).filter(Boolean).map(part => part[0]).slice(0, 2).join('');
  return <div className="app-shell">
    <aside className="sidebar">
      <div className="sidebar-brand"><span className="brand-mark">A</span><span className="brand">ative</span><small>CRM</small></div>
      <nav aria-label="Navegação principal">
        <p className="nav-group">Principal</p>
        {pages.map(item => <div key={item.key}>
          {item.group && <p className="nav-group">{item.group}</p>}
          <button aria-current={item.key === page ? 'page' : undefined}
            onClick={() => { setPage(item.key); setSelected(null); }}>
            <span aria-hidden="true" className="nav-icon">{item.icon}</span>{item.label}
          </button>
        </div>)}
      </nav>
      <div className="sidebar-footer"><strong>Gestão comercial</strong>
        <span>{data?.user.profile === 'GESTOR' ? 'Visão da equipe' : 'Sua carteira de oportunidades'}</span>
      </div>
    </aside>
    <div className="app-main">
      <header className="top">
        <p className="breadcrumb">Comercial <span>/</span> <strong>{pages.find(item => item.key === page).label}</strong></p>
        <div className="account">
          <span className="account-name">{data?.user.name}<small>{data?.user.profile === 'GESTOR' ? 'Gestor · equipe' : 'Vendedor · minha carteira'}</small></span>
          <span className="avatar" aria-hidden="true">{initials || '·'}</span>
          <button className="logout" onClick={logout}>Sair</button>
        </div>
      </header>
      <main className="workspace">
        <div className="page-heading">
          <div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1><p className="page-description">{description}</p></div>
          <div className="heading-actions">
            <button className="secondary refresh-button" onClick={refresh} aria-label="Atualizar dados" title="Atualizar dados">↻</button>
            {['Funil', 'Agenda'].includes(page) && <button onClick={() => setPage('Leads')}>+ Novo lead</button>}
          </div>
        </div>
        <ErrorMessage error={error} />
        {!data && !error && <p role="status" className="loading">Carregando CRM…</p>}
        {data && page === 'Indicadores' && <Indicadores data={data} onOpen={openOpportunity} stalledDays={stalledDays} setStalledDays={setStalledDays} />}
        {data && page === 'Agenda' && <Agenda data={data} onOpen={openOpportunity} stalledDays={stalledDays} setStalledDays={setStalledDays} />}
        {data && page === 'Empresas' && <Empresas empresas={data.empresas} save={save} />}
        {data && page === 'Leads' && <Leads empresas={data.empresas} leads={data.leads} save={save} refresh={refresh} />}
        {data && page === 'Funil' && <Funil stalledDays={stalledDays} data={data} save={save} onOpen={openOpportunity} />}
      </main>
    </div>
    {opportunity && <Oportunidade key={selected} item={opportunity} data={data} stalledDays={stalledDays} save={save} onBack={() => setSelected(null)} />}
  </div>;
}

export default function App() {
  const [token, setToken] = useState(() => sessionStorage.getItem(sessionKey));
  const [expired, setExpired] = useState('');
  const endSession = useCallback((expected, message = '') => {
    if (sessionStorage.getItem(sessionKey) !== expected) return;
    sessionStorage.removeItem(sessionKey);
    setToken(null);
    setExpired(message);
  }, []);
  const api = useCallback(async (path, options) => {
    try {
      return await request(path, { ...options, token });
    } catch (error) {
      if (error.status === 401) endSession(token, 'Sua sessão terminou. Entre novamente.');
      throw error;
    }
  }, [token, endSession]);
  function login(value) {
    if (!value) throw new Error('A API não retornou um token.');
    sessionStorage.setItem(sessionKey, value);
    setToken(value);
    setExpired('');
  }
  return token ? <Workspace api={api} logout={() => endSession(token)} />
    : <Access onLogin={login} expired={expired} />;
}
