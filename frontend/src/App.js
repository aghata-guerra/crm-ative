
import React, { useEffect, useState } from 'react';

const API_URL = 'http://localhost:8080';

const estilos = {
  pagina: {
    fontFamily: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif',
    backgroundColor: '#f8f9fa',
    minHeight: '100vh',
    margin: 0,
  },
  campo: {
    width: '100%',
    padding: '11px',
    borderRadius: '6px',
    border: '1px solid #d4d4d8',
    boxSizing: 'border-box',
  },
  botaoVerde: {
    backgroundColor: '#22c55e',
    color: '#000',
    border: 'none',
    padding: '12px 25px',
    borderRadius: '25px',
    fontWeight: 'bold',
    cursor: 'pointer',
  },
  botaoCinza: {
    backgroundColor: '#e4e4e7',
    color: '#3f3f46',
    border: 'none',
    padding: '10px 18px',
    borderRadius: '6px',
    fontWeight: 'bold',
    cursor: 'pointer',
  },
  label: {
    display: 'block',
    fontSize: '0.85rem',
    marginBottom: '5px',
    color: '#52525b',
  },
  celula: {
    padding: '15px',
    borderBottom: '1px solid #e4e4e7',
  },
};

async function requisicao(caminho, opcoes = {}, token = null) {
  const resposta = await fetch(`${API_URL}${caminho}`, {
    ...opcoes,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...opcoes.headers,
    },
  });

  if (!resposta.ok) {
    if (resposta.status === 401) {
      throw new Error('Sessão inválida ou credenciais incorretas.');
    }

    if (resposta.status === 403) {
      throw new Error('Você não tem permissão para realizar esta operação.');
    }

    let detalhe = '';

    try {
      const erro = await resposta.json();
      detalhe = erro.message || erro.detail || erro.error || '';
    } catch (_) {
      // Algumas respostas de erro não possuem JSON.
    }

    throw new Error(
      detalhe || `Erro na requisição. Código HTTP: ${resposta.status}`
    );
  }

  if (resposta.status === 204) {
    return null;
  }

  return resposta.json();
}

function App() {
  // Autenticação
  const [token, setToken] = useState(null);
  const [emailLogin, setEmailLogin] = useState('');
  const [senhaLogin, setSenhaLogin] = useState('');
  const [carregandoLogin, setCarregandoLogin] = useState(false);
  const [erroLogin, setErroLogin] = useState('');
  const [modoCadastro, setModoCadastro] = useState(false);
  const [nomeCadastroUsuario, setNomeCadastroUsuario] = useState('');
  const [emailCadastroUsuario, setEmailCadastroUsuario] = useState('');
  const [senhaCadastroUsuario, setSenhaCadastroUsuario] = useState('');
  const [confirmacaoSenhaUsuario, setConfirmacaoSenhaUsuario] = useState('');
  const [carregandoCadastroUsuario, setCarregandoCadastroUsuario] = useState(false);
  const [erroCadastroUsuario, setErroCadastroUsuario] = useState('');
  const [sucessoCadastroUsuario, setSucessoCadastroUsuario] = useState('');

  // Dados reais da API
  const [leads, setLeads] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [totalLeads, setTotalLeads] = useState(0);
  const [carregando, setCarregando] = useState(false);
  const [erroDados, setErroDados] = useState('');

  // Modal de cadastro
  const [mostrarModal, setMostrarModal] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [erroCadastro, setErroCadastro] = useState('');

  const [novoNome, setNovoNome] = useState('');
  const [novoEmail, setNovoEmail] = useState('');
  const [novoTelefone, setNovoTelefone] = useState('');
  const [novoCargo, setNovoCargo] = useState('');
  const [novaEmpresaId, setNovaEmpresaId] = useState('');

  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  // Carrega os dados após a autenticação
  useEffect(() => {
    if (!token) return;

    let ativo = true;

    async function carregar() {
      setCarregando(true);
      setErroDados('');

      try {
        const [dadosLeads, dadosEmpresas] = await Promise.all([
          requisicao(`/lead?page=${paginaAtual}&size=10&sort=id,asc`, {}, token),
          requisicao('/empresa?page=0&size=100&sort=id,asc', {}, token),
        ]);

        if (!ativo) return;

        setLeads(dadosLeads.content || []);
        setTotalLeads(dadosLeads.totalElements || 0);
        setTotalPaginas(dadosLeads.totalPages || 0);
        setEmpresas(dadosEmpresas.content || []);
      } catch (erro) {
        if (ativo) {
          setErroDados(erro.message);
        }
      } finally {
        if (ativo) setCarregando(false);
      }
    }

    carregar();

    return () => {
      ativo = false;
    };
  }, [token, paginaAtual]);

  async function fazerLogin(evento) {
    evento.preventDefault();

    setCarregandoLogin(true);
    setErroLogin('');

    try {
      const resposta = await requisicao('/auth/login', {
        method: 'POST',
        body: JSON.stringify({
          email: emailLogin.trim(),
          password: senhaLogin,
        }),
      });

      // O DTO do backend utiliza "acessToken".
      if (!resposta.acessToken) {
        throw new Error('O backend não retornou um token válido.');
      }

      setSenhaLogin('');
      setPaginaAtual(0);
      setToken(resposta.acessToken);
    } catch (erro) {
      setErroLogin(erro.message);
    } finally {
      setCarregandoLogin(false);
    }
  }

  async function cadastrarUsuario(evento) {
    evento.preventDefault();
    setErroCadastroUsuario('');

    if (senhaCadastroUsuario !== confirmacaoSenhaUsuario) {
      setErroCadastroUsuario('As senhas não coincidem.');
      return;
    }

    setCarregandoCadastroUsuario(true);
    try {
      await requisicao('/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          name: nomeCadastroUsuario.trim(),
          email: emailCadastroUsuario.trim(),
          password: senhaCadastroUsuario,
        }),
      });
      setEmailLogin(emailCadastroUsuario.trim());
      setSenhaLogin('');
      setNomeCadastroUsuario('');
      setEmailCadastroUsuario('');
      setSenhaCadastroUsuario('');
      setConfirmacaoSenhaUsuario('');
      setModoCadastro(false);
      setErroLogin('');
      setSucessoCadastroUsuario('Conta criada com sucesso! Faça login para continuar.');
    } catch (erro) {
      setErroCadastroUsuario(erro.message);
    } finally {
      setCarregandoCadastroUsuario(false);
    }
  }

  function alternarModoCadastro() {
    setModoCadastro((anterior) => !anterior);
    setErroLogin('');
    setErroCadastroUsuario('');
    setSucessoCadastroUsuario('');
  }

  function sairDaConta() {
    setToken(null);
    setSenhaLogin('');
    setLeads([]);
    setEmpresas([]);
    setTotalLeads(0);
    setPaginaAtual(0);
    setMostrarModal(false);
    setErroDados('');
  }

  async function atualizarDados() {
    if (!token) return;

    setCarregando(true);
    setErroDados('');

    try {
      const [dadosLeads, dadosEmpresas] = await Promise.all([
        requisicao(`/lead?page=${paginaAtual}&size=10&sort=id,asc`, {}, token),
        requisicao('/empresa?page=0&size=100&sort=id,asc', {}, token),
      ]);

      setLeads(dadosLeads.content || []);
      setTotalLeads(dadosLeads.totalElements || 0);
      setTotalPaginas(dadosLeads.totalPages || 0);
      setEmpresas(dadosEmpresas.content || []);
    } catch (erro) {
      setErroDados(erro.message);
    } finally {
      setCarregando(false);
    }
  }

  function limparFormulario() {
    setNovoNome('');
    setNovoEmail('');
    setNovoTelefone('');
    setNovoCargo('');
    setNovaEmpresaId('');
    setErroCadastro('');
  }

  function fecharModal() {
    if (salvando) return;

    setMostrarModal(false);
    limparFormulario();
  }

  async function adicionarLead(evento) {
    evento.preventDefault();

    if (
      !novoNome.trim() ||
      !novoEmail.trim() ||
      !novoTelefone.trim() ||
      !novoCargo.trim() ||
      !novaEmpresaId
    ) {
      setErroCadastro('Preencha todos os campos obrigatórios.');
      return;
    }

    setSalvando(true);
    setErroCadastro('');

    try {
      await requisicao(
        '/lead',
        {
          method: 'POST',
          body: JSON.stringify({
            name: novoNome.trim(),
            email: novoEmail.trim(),
            telephone: novoTelefone.trim(),
            position: novoCargo.trim(),
            empresaId: Number(novaEmpresaId),
          }),
        },
        token
      );

      fecharModalDepoisDeSalvar();
      setPaginaAtual(0);

      // Atualiza a listagem após cadastrar.
      const dados = await requisicao(
        '/lead?page=0&size=10&sort=id,asc',
        {},
        token
      );

      setLeads(dados.content || []);
      setTotalLeads(dados.totalElements || 0);
      setTotalPaginas(dados.totalPages || 0);
    } catch (erro) {
      setErroCadastro(erro.message);
    } finally {
      setSalvando(false);
    }
  }

  function fecharModalDepoisDeSalvar() {
    setMostrarModal(false);
    limparFormulario();
  }

  function nomeDaEmpresa(empresaId) {
    const empresa = empresas.find(
      (item) => Number(item.id) === Number(empresaId)
    );

    return empresa
      ? empresa.tradeName || empresa.companyName
      : `Empresa #${empresaId}`;
  }

  // TELA DE LOGIN
  if (!token) {
    return (
      <div
        style={{
          ...estilos.pagina,
          backgroundColor: '#111111',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '20px',
          boxSizing: 'border-box',
        }}
      >
        <div
          style={{
            backgroundColor: '#18181b',
            padding: '40px',
            borderRadius: '16px',
            border: '1px solid #3f3f46',
            width: '100%',
            maxWidth: '420px',
            boxSizing: 'border-box',
            boxShadow: '0 15px 45px rgba(0,0,0,0.3)',
          }}
        >
          <div style={{ textAlign: 'center', marginBottom: '30px' }}>
            <h1
              style={{
                fontSize: '2.8rem',
                color: 'white',
                margin: '0 0 8px',
              }}
            >
              á<span style={{ color: '#22c55e' }}>tive</span>.
            </h1>

            <p style={{ color: '#a1a1aa', margin: 0 }}>
              CRM Comercial
            </p>
          </div>

          <h2
            style={{
              color: 'white',
              fontSize: '1.4rem',
              marginBottom: '8px',
            }}
          >
            {modoCadastro ? 'Criar conta' : 'Bem-vindo de volta'}
          </h2>

          <p
            style={{
              color: '#a1a1aa',
              fontSize: '0.9rem',
              marginBottom: '25px',
            }}
          >
            {modoCadastro
              ? 'Cadastre-se para começar a usar o CRM.'
              : 'Entre com sua conta para acessar o CRM.'}
          </p>

          {sucessoCadastroUsuario && !modoCadastro && (
            <p role="status" style={{ color: '#86efac', fontSize: '0.9rem' }}>
              {sucessoCadastroUsuario}
            </p>
          )}

          {modoCadastro ? (
            <form onSubmit={cadastrarUsuario} style={{ display: 'flex', flexDirection: 'column', gap: '17px' }}>
              <div>
                <label htmlFor="nome-cadastro" style={{ ...estilos.label, color: '#d4d4d8' }}>Nome completo</label>
                <input id="nome-cadastro" type="text" value={nomeCadastroUsuario}
                  onChange={(e) => setNomeCadastroUsuario(e.target.value)}
                  autoComplete="name" required maxLength={100} style={estilos.campo} />
              </div>
              <div>
                <label htmlFor="email-cadastro" style={{ ...estilos.label, color: '#d4d4d8' }}>E-mail</label>
                <input id="email-cadastro" type="email" value={emailCadastroUsuario}
                  onChange={(e) => setEmailCadastroUsuario(e.target.value)}
                  autoComplete="email" required style={estilos.campo} />
              </div>
              <div>
                <label htmlFor="senha-cadastro" style={{ ...estilos.label, color: '#d4d4d8' }}>Senha</label>
                <input id="senha-cadastro" type="password" value={senhaCadastroUsuario}
                  onChange={(e) => setSenhaCadastroUsuario(e.target.value)}
                  autoComplete="new-password" required minLength={8} style={estilos.campo} />
              </div>
              <div>
                <label htmlFor="confirmar-senha" style={{ ...estilos.label, color: '#d4d4d8' }}>Confirmar senha</label>
                <input id="confirmar-senha" type="password" value={confirmacaoSenhaUsuario}
                  onChange={(e) => setConfirmacaoSenhaUsuario(e.target.value)}
                  autoComplete="new-password" required minLength={8} style={estilos.campo} />
              </div>
              {erroCadastroUsuario && (
                <p role="alert" style={{ color: '#f87171', fontSize: '0.9rem' }}>{erroCadastroUsuario}</p>
              )}
              <button type="submit" disabled={carregandoCadastroUsuario}
                style={{ ...estilos.botaoVerde, opacity: carregandoCadastroUsuario ? 0.6 : 1 }}>
                {carregandoCadastroUsuario ? 'Criando conta...' : 'Criar conta'}
              </button>
            </form>
          ) : (
            <form onSubmit={fazerLogin} style={{ display: 'flex', flexDirection: 'column', gap: '17px' }}>
              <div>
                <label htmlFor="email-login" style={{ ...estilos.label, color: '#d4d4d8' }}>E-mail</label>
                <input id="email-login" type="email" value={emailLogin}
                  onChange={(e) => setEmailLogin(e.target.value)}
                  placeholder="seuemail@empresa.com" autoComplete="username"
                  required style={estilos.campo} />
              </div>
              <div>
                <label htmlFor="senha-login" style={{ ...estilos.label, color: '#d4d4d8' }}>Senha</label>
                <input id="senha-login" type="password" value={senhaLogin}
                  onChange={(e) => setSenhaLogin(e.target.value)}
                  placeholder="Digite sua senha" autoComplete="current-password"
                  required style={estilos.campo} />
              </div>
              {erroLogin && (
                <p role="alert" style={{ color: '#f87171', fontSize: '0.9rem' }}>{erroLogin}</p>
              )}
              <button type="submit" disabled={carregandoLogin}
                style={{ ...estilos.botaoVerde, marginTop: '8px', opacity: carregandoLogin ? 0.6 : 1 }}>
                {carregandoLogin ? 'Entrando...' : 'Entrar no CRM'}
              </button>
            </form>
          )}

          <p style={{ textAlign: 'center', color: '#a1a1aa', fontSize: '0.9rem', marginTop: '20px' }}>
            {modoCadastro ? 'Já tem uma conta?' : 'Ainda não tem uma conta?'}{' '}
            <button type="button" onClick={alternarModoCadastro}
              style={{ background: 'none', border: 'none', color: '#22c55e', cursor: 'pointer', fontWeight: 'bold' }}>
              {modoCadastro ? 'Entrar' : 'Criar conta'}
            </button>
          </p>

          <p
            style={{
              textAlign: 'center',
              color: '#71717a',
              fontSize: '0.75rem',
              marginTop: '25px',
            }}
          >
            CRM-ATIVE • Sistema de gestão comercial
          </p>
        </div>
      </div>
    );
  }

  // DASHBOARD
  return (
    <div style={estilos.pagina}>
      {/* Cabeçalho original */}
      <header
        style={{
          backgroundColor: '#111111',
          color: 'white',
          padding: '20px 40px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '20px',
          borderBottom: '1px solid #222',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span
            style={{
              fontSize: '1.5rem',
              fontWeight: 'bold',
              color: 'white',
            }}
          >
            á<span style={{ color: '#22c55e' }}>tive</span>.
          </span>

          <span
            style={{
              fontSize: '0.85rem',
              color: '#888',
              borderLeft: '1px solid #444',
              paddingLeft: '10px',
            }}
          >
            CRM Comercial
          </span>
        </div>

        <nav
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '20px',
            fontSize: '0.95rem',
            flexWrap: 'wrap',
          }}
        >
          <span style={{ color: '#22c55e', fontWeight: '600' }}>
            Início
          </span>

          <span style={{ color: '#ccc' }}>Leads</span>
          <span style={{ color: '#ccc' }}>Funil de Vendas</span>
          <span style={{ color: '#ccc' }}>Relatórios</span>

          <button
            onClick={sairDaConta}
            style={{
              backgroundColor: 'transparent',
              color: '#f87171',
              border: '1px solid #52525b',
              padding: '8px 15px',
              borderRadius: '6px',
              cursor: 'pointer',
            }}
          >
            Sair
          </button>
        </nav>
      </header>

      {/* Banner original */}
      <section
        style={{
          backgroundColor: '#18181b',
          color: 'white',
          padding: '50px 40px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '30px',
        }}
      >
        <div style={{ maxWidth: '600px' }}>
          <h1
            style={{
              fontSize: '2.5rem',
              margin: '0 0 15px',
              lineHeight: '1.2',
            }}
          >
            Tenha os leads da sua{' '}
            <span style={{ color: '#22c55e' }}>
              empresa organizados
            </span>{' '}
            e feche mais negócios.
          </h1>

          <p
            style={{
              color: '#a1a1aa',
              fontSize: '1.1rem',
              marginBottom: '25px',
            }}
          >
            Centralize suas oportunidades, acompanhe o funil de
            vendas e potencialize sua conversão mensal.
          </p>

          <button
            onClick={() => {
              setErroCadastro('');
              setMostrarModal(true);
            }}
            style={estilos.botaoVerde}
          >
            + Novo Lead
          </button>
        </div>

        <div
          style={{
            backgroundColor: '#27272a',
            padding: '20px',
            borderRadius: '12px',
            border: '1px solid #3f3f46',
            width: '350px',
            maxWidth: '100%',
            boxSizing: 'border-box',
          }}
        >
          <h3
            style={{
              margin: '0 0 10px',
              fontSize: '1rem',
              color: '#22c55e',
            }}
          >
            Resumo do Mês (Squad 53)
          </h3>

          <p style={{ color: '#d4d4d8', fontSize: '0.9rem' }}>
            Meta de Leads: <strong>200 / mês</strong>
          </p>

          <p style={{ color: '#d4d4d8', fontSize: '0.9rem' }}>
            Leads cadastrados:{' '}
            <strong>{totalLeads}</strong>
          </p>
          <p style={{ color: '#a1a1aa', fontSize: '0.75rem' }}>
            Total geral registrado no sistema.
          </p>
        </div>
      </section>

      {/* Modal de cadastro */}
      {mostrarModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0,0,0,0.65)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000,
            padding: '20px',
            boxSizing: 'border-box',
          }}
        >
          <div
            style={{
              backgroundColor: 'white',
              padding: '30px',
              borderRadius: '12px',
              width: '450px',
              maxWidth: '100%',
              maxHeight: '90vh',
              overflowY: 'auto',
              boxSizing: 'border-box',
              boxShadow: '0 4px 20px rgba(0,0,0,0.2)',
            }}
          >
            <h3 style={{ margin: '0 0 20px', color: '#18181b' }}>
              Cadastrar Novo Lead
            </h3>

            <form
              onSubmit={adicionarLead}
              style={{
                display: 'flex',
                flexDirection: 'column',
                gap: '15px',
              }}
            >
              <div>
                <label style={estilos.label}>Nome do cliente</label>
                <input
                  type="text"
                  value={novoNome}
                  onChange={(e) => setNovoNome(e.target.value)}
                  placeholder="Ex: Maria Oliveira"
                  maxLength={100}
                  required
                  style={estilos.campo}
                />
              </div>

              <div>
                <label style={estilos.label}>E-mail</label>
                <input
                  type="email"
                  value={novoEmail}
                  onChange={(e) => setNovoEmail(e.target.value)}
                  placeholder="cliente@empresa.com"
                  required
                  style={estilos.campo}
                />
              </div>

              <div>
                <label style={estilos.label}>Telefone</label>
                <input
                  type="tel"
                  value={novoTelefone}
                  onChange={(e) => setNovoTelefone(e.target.value)}
                  placeholder="81999999999"
                  maxLength={12}
                  required
                  style={estilos.campo}
                />
              </div>

              <div>
                <label style={estilos.label}>Cargo / Posição</label>
                <input
                  type="text"
                  value={novoCargo}
                  onChange={(e) => setNovoCargo(e.target.value)}
                  placeholder="Ex: Gerente Comercial"
                  required
                  style={estilos.campo}
                />
              </div>

              <div>
                <label style={estilos.label}>Empresa vinculada</label>
                <select
                  value={novaEmpresaId}
                  onChange={(e) => setNovaEmpresaId(e.target.value)}
                  required
                  style={estilos.campo}
                >
                  <option value="">Selecione uma empresa</option>

                  {empresas.map((empresa) => (
                    <option key={empresa.id} value={empresa.id}>
                      {empresa.tradeName || empresa.companyName}
                    </option>
                  ))}
                </select>

                {empresas.length === 0 && (
                  <p style={{ fontSize: '0.8rem', color: '#71717a' }}>
                    Nenhuma empresa disponível. Cadastre uma
                    empresa no backend primeiro.
                  </p>
                )}
              </div>

              {erroCadastro && (
                <p style={{ color: '#dc2626', fontSize: '0.9rem' }}>
                  {erroCadastro}
                </p>
              )}

              <div
                style={{
                  display: 'flex',
                  gap: '10px',
                  marginTop: '10px',
                }}
              >
                <button
                  type="submit"
                  disabled={salvando || empresas.length === 0}
                  style={{
                    ...estilos.botaoVerde,
                    flex: 1,
                    borderRadius: '6px',
                    opacity: salvando ? 0.6 : 1,
                  }}
                >
                  {salvando ? 'Salvando...' : 'Salvar Lead'}
                </button>

                <button
                  type="button"
                  onClick={fecharModal}
                  disabled={salvando}
                  style={{ ...estilos.botaoCinza, flex: 1 }}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Gestão de oportunidades */}
      <main style={{ padding: '40px' }}>
        <section
          style={{
            backgroundColor: 'white',
            padding: '30px',
            borderRadius: '12px',
            boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)',
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              gap: '15px',
              flexWrap: 'wrap',
              marginBottom: '20px',
            }}
          >
            <h2
              style={{
                margin: 0,
                fontSize: '1.3rem',
                color: '#18181b',
              }}
            >
              Gestão de Oportunidades ({totalLeads})
            </h2>

            <button
              onClick={atualizarDados}
              disabled={carregando}
              style={estilos.botaoCinza}
            >
              {carregando ? 'Atualizando...' : 'Atualizar'}
            </button>
          </div>

          {erroDados && (
            <p style={{ color: '#dc2626' }}>
              Erro ao carregar dados: {erroDados}
            </p>
          )}

          {carregando && (
            <p style={{ color: '#71717a' }}>
              Carregando dados do CRM...
            </p>
          )}

          <div style={{ overflowX: 'auto' }}>
            <table
              style={{
                width: '100%',
                borderCollapse: 'collapse',
                textAlign: 'left',
              }}
            >
              <thead>
                <tr
                  style={{
                    backgroundColor: '#f4f4f5',
                    color: '#52525b',
                    fontSize: '0.85rem',
                    textTransform: 'uppercase',
                  }}
                >
                  <th style={{ padding: '15px' }}>ID</th>
                  <th style={{ padding: '15px' }}>Cliente</th>
                  <th style={{ padding: '15px' }}>Empresa</th>
                  <th style={{ padding: '15px' }}>E-mail</th>
                  <th style={{ padding: '15px' }}>Telefone</th>
                  <th style={{ padding: '15px' }}>Cargo</th>
                </tr>
              </thead>

              <tbody>
                {leads.map((lead) => (
                  <tr key={lead.id} style={{ fontSize: '0.95rem' }}>
                    <td style={{ ...estilos.celula, color: '#71717a' }}>
                      #{lead.id}
                    </td>

                    <td
                      style={{
                        ...estilos.celula,
                        fontWeight: '600',
                        color: '#18181b',
                      }}
                    >
                      {lead.name}
                    </td>

                    <td style={estilos.celula}>
                      {nomeDaEmpresa(lead.empresaId)}
                    </td>

                    <td style={estilos.celula}>{lead.email}</td>
                    <td style={estilos.celula}>{lead.telephone}</td>
                    <td style={estilos.celula}>{lead.position}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {!carregando && leads.length === 0 && !erroDados && (
              <p
                style={{
                  textAlign: 'center',
                  color: '#71717a',
                  padding: '25px',
                }}
              >
                Nenhum lead cadastrado.
              </p>
            )}
          </div>

          {totalPaginas > 1 && (
            <div
              style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                gap: '15px',
                marginTop: '25px',
              }}
            >
              <button
                disabled={paginaAtual === 0 || carregando}
                onClick={() => setPaginaAtual((p) => p - 1)}
                style={estilos.botaoCinza}
              >
                Anterior
              </button>

              <span style={{ color: '#52525b' }}>
                Página {paginaAtual + 1} de {totalPaginas}
              </span>

              <button
                disabled={
                  paginaAtual >= totalPaginas - 1 || carregando
                }
                onClick={() => setPaginaAtual((p) => p + 1)}
                style={estilos.botaoCinza}
              >
                Próxima
              </button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
