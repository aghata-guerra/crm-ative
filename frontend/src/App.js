import { useCallback, useEffect, useState } from 'react';
import { list, request } from './api';
import { ActionForm, ErrorMessage, Field } from './components';
import { Empresas, Leads } from './cadastros';
import { Funil, Indicadores, Oportunidade } from './oportunidades';
import './App.css';

const pages = ['Visão geral', 'Empresas', 'Leads', 'Funil'];
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
  const [page, setPage] = useState('Visão geral');
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
  return <>
    <header className="top">
      <span className="brand">ative<span>.</span></span>
      <nav aria-label="Navegação principal">{pages.map(item => <button key={item}
        aria-current={item === page ? 'page' : undefined}
        onClick={() => { setPage(item); setSelected(null); }}>{item}</button>)}</nav>
      <div className="account">{data?.user.name}<button className="secondary" onClick={logout}>Sair</button></div>
    </header>
    <main className="workspace">
      <div className="page-heading"><h1>{page}</h1><button className="secondary" onClick={refresh}>Atualizar dados</button></div>
      <ErrorMessage error={error} />
      {!data && !error && <p role="status">Carregando CRM…</p>}
      {data && page === 'Visão geral' && <Indicadores data={data} />}
      {data && page === 'Empresas' && <Empresas empresas={data.empresas} save={save} />}
      {data && page === 'Leads' && <Leads empresas={data.empresas} leads={data.leads} save={save} refresh={refresh} />}
      {data && page === 'Funil' && (opportunity
        ? <Oportunidade key={selected} item={opportunity} data={data} save={save} onBack={() => setSelected(null)} />
        : <Funil data={data} save={save} onOpen={setSelected} />)}
    </main>
  </>;
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
