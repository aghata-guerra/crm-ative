import React, { useState } from 'react';

function App() {
  // Estado com a lista inicial de leads
  const [leads, setLeads] = useState([
    { id: 1, nome: 'Empresa Alpha', origem: 'Indicação', status: 'Em Negociação', responsavel: 'Thales' },
    { id: 2, nome: 'Comércio Beta', origem: 'Redes Sociais', status: 'Proposta Enviada', responsavel: 'Aghata' },
    { id: 3, nome: 'Serviços Gamma', origem: 'Site', status: 'Novo Lead', responsavel: 'Inadilson' },
  ]);

  // Estados para controlar o formulário de novo lead
  const [mostrarModal, setMostrarModal] = useState(false);
  const [novoNome, setNovoNome] = useState('');
  const [novaOrigem, setNovaOrigem] = useState('Site');
  const [novoResponsavel, setNovoResponsavel] = useState('');

  // Função para adicionar um novo lead na lista
  const adicionarLead = (e) => {
    e.preventDefault();
    if (!novoNome.trim() || !novoResponsavel.trim()) return;

    const novoLeadObj = {
      id: leads.length + 1,
      nome: novoNome,
      origem: novaOrigem,
      status: 'Novo Lead',
      responsavel: novoResponsavel,
    };

    setLeads([...leads, novoLeadObj]);
    setNovoNome('');
    setNovoResponsavel('');
    setMostrarModal(false); // Fecha o modal após salvar
  };

  return (
    <div style={{ fontFamily: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif', backgroundColor: '#f8f9fa', minHeight: '100vh', margin: 0 }}>
      
      {/* Cabeçalho / Navbar estilo Ative (Fundo Escuro) */}
      <header style={{ backgroundColor: '#111111', color: 'white', padding: '20px 40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #222' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white' }}>
            á<span style={{ color: '#22c55e' }}>tive</span>.
          </span>
          <span style={{ fontSize: '0.85rem', color: '#888', borderLeft: '1px solid #444', paddingLeft: '10px' }}>CRM Comercial</span>
        </div>
        <nav style={{ display: 'flex', gap: '25px', fontSize: '0.95rem' }}>
          <span style={{ color: '#22c55e', fontWeight: '600', cursor: 'pointer' }}>Início</span>
          <span style={{ color: '#ccc', cursor: 'pointer' }}>Leads</span>
          <span style={{ color: '#ccc', cursor: 'pointer' }}>Funil de Vendas</span>
          <span style={{ color: '#ccc', cursor: 'pointer' }}>Relatórios</span>
        </nav>
      </header>

      {/* Banner Superior Estilo Ative */}
      <section style={{ backgroundColor: '#18181b', color: 'white', padding: '50px 40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ maxWidth: '600px' }}>
          <h1 style={{ fontSize: '2.5rem', margin: '0 0 15px 0', lineHeight: '1.2' }}>
            Tenha os leads da sua <span style={{ color: '#22c55e' }}>empresa organizados</span> e feche mais negócios.
          </h1>
          <p style={{ color: '#a1a1aa', fontSize: '1.1rem', marginBottom: '25px' }}>
            Centralize suas oportunidades, acompanhe o funil de vendas e potencialize sua conversão mensal.
          </p>
          <button 
            onClick={() => setMostrarModal(true)}
            style={{ backgroundColor: '#22c55e', color: '#000', border: 'none', padding: '12px 25px', borderRadius: '25px', fontWeight: 'bold', fontSize: '1rem', cursor: 'pointer' }}
          >
            + Novo Lead
          </button>
        </div>
        <div style={{ backgroundColor: '#27272a', padding: '20px', borderRadius: '12px', border: '1px solid #3f3f46', width: '350px' }}>
          <h3 style={{ margin: '0 0 10px 0', fontSize: '1rem', color: '#22c55e' }}>Resumo do Mês (Squad 53)</h3>
          <p style={{ margin: '5px 0', color: '#d4d4d8', fontSize: '0.9rem' }}>Meta de Leads: <strong>200 / mês</strong></p>
          <p style={{ margin: '5px 0', color: '#d4d4d8', fontSize: '0.9rem' }}>Leads Atuais: <strong>{leads.length} cadastrados</strong></p>
        </div>
      </section>

      {/* Modal / Formulário para Adicionar Lead */}
      {mostrarModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', width: '400px', boxShadow: '0 4px 12px rgba(0,0,0,0.2)' }}>
            <h3 style={{ margin: '0 0 20px 0', color: '#18181b' }}>Cadastrar Novo Lead</h3>
            
            <form onSubmit={adicionarLead} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '5px', color: '#52525b' }}>Nome da Empresa / Cliente</label>
                <input 
                  type="text" 
                  value={novoNome} 
                  onChange={(e) => setNovoNome(e.target.value)} 
                  placeholder="Ex: Empresa Delta"
                  required
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d4d4d8', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '5px', color: '#52525b' }}>Origem do Lead</label>
                <select 
                  value={novaOrigem} 
                  onChange={(e) => setNovaOrigem(e.target.value)}
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d4d4d8', boxSizing: 'border-box' }}
                >
                  <option value="Site">Site</option>
                  <option value="WhatsApp">WhatsApp</option>
                  <option value="Indicação">Indicação</option>
                  <option value="Redes Sociais">Redes Sociais</option>
                </select>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '5px', color: '#52525b' }}>Responsável Comercial</label>
                <input 
                  type="text" 
                  value={novoResponsavel} 
                  onChange={(e) => setNovoResponsavel(e.target.value)} 
                  placeholder="Ex: Thales / Aghata"
                  required
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d4d4d8', boxSizing: 'border-box' }}
                />
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                <button 
                  type="submit" 
                  style={{ flex: 1, backgroundColor: '#22c55e', color: '#000', border: 'none', padding: '10px', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                >
                  Salvar Lead
                </button>
                <button 
                  type="button" 
                  onClick={() => setMostrarModal(false)}
                  style={{ flex: 1, backgroundColor: '#e4e4e7', color: '#3f3f46', border: 'none', padding: '10px', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Tabela de Leads / Conteúdo Principal */}
      <main style={{ padding: '40px' }}>
        <section style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }}>
          <h2 style={{ margin: '0 0 20px 0', fontSize: '1.3rem', color: '#18181b' }}>Gestão de Oportunidades ({leads.length})</h2>
          
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ backgroundColor: '#f4f4f5', color: '#52525b', fontSize: '0.85rem', textTransform: 'uppercase' }}>
                <th style={{ padding: '15px' }}>ID</th>
                <th style={{ padding: '15px' }}>Cliente / Empresa</th>
                <th style={{ padding: '15px' }}>Origem</th>
                <th style={{ padding: '15px' }}>Status</th>
                <th style={{ padding: '15px' }}>Responsável</th>
              </tr>
            </thead>
            <tbody>
              {leads.map((lead) => (
                <tr key={lead.id} style={{ borderBottom: '1px solid #e4e4e7', fontSize: '0.95rem' }}>
                  <td style={{ padding: '15px', color: '#71717a' }}>#{lead.id}</td>
                  <td style={{ padding: '15px', fontWeight: '600', color: '#18181b' }}>{lead.nome}</td>
                  <td style={{ padding: '15px', color: '#52525b' }}>{lead.origem}</td>
                  <td style={{ padding: '15px' }}>
                    <span style={{ backgroundColor: '#dcfce7', color: '#166534', padding: '6px 12px', borderRadius: '20px', fontSize: '0.8rem', fontWeight: '600' }}>
                      {lead.status}
                    </span>
                  </td>
                  <td style={{ padding: '15px', color: '#52525b' }}>{lead.responsavel}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default App;