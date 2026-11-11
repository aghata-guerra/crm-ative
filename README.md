# CRM Ative OS — Sistema de Gestão Comercial (Squad 53)

> Sistema desenvolvido para centralizar a gestão de leads, otimizar o acompanhamento do funil de vendas e apoiar os processos comerciais.

---

## Sobre o Projeto

O **CRM Ative OS** foi idealizado para solucionar o desafio do controle manual de leads e prospecções, oferecendo uma interface para equipes comerciais.

O projeto está sendo desenvolvido pelo **Squad 53**, com integração entre frontend React e backend Spring Boot.

## Tecnologias Utilizadas

- **Frontend:** React.js, JavaScript, HTML e CSS.
- **Backend:** Java, Spring Boot, Spring Security e Spring Data JPA.
- **Autenticação:** JWT.
- **Banco de desenvolvimento:** H2, com persistência em arquivo.
- **Banco alternativo:** MySQL.
- **Gerenciamento do backend:** Maven Wrapper.
- **Controle de versão:** Git e GitHub.

## Funcionalidades

- Cadastro de usuários e autenticação com JWT.
- Cadastro de empresas e leads, com origem e importação de leads por CSV.
- Funil por etapa, histórico, diagnóstico, próximos passos e fechamento com motivo de perda.
- Painel com alertas no CRM e indicadores de vendas.

---
## Como Executar Localmente

### Pré-requisitos

- Git.
- Node.js e npm.
- JDK compatível com a versão configurada no `pom.xml` (ambiente testado com Java 25).

O projeto utiliza o Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

### 1. Abrir o repositório local

Na pasta que contém os diretórios `CRM` e `frontend`, selecione a branch da entrega:

```powershell
git switch feat/finalizacao-crm
```
### 2. Configurar o backend

Abra um terminal PowerShell na pasta `CRM`:

```powershell
cd CRM
```

Configure a variável de ambiente `JWT_SECRET` com um segredo aleatório para desenvolvimento:

```powershell
$env:JWT_SECRET = -join ((1..64) | ForEach-Object { '{0:x}' -f (Get-Random -Maximum 16) })
```

**Importante:** não envie o valor do segredo para o GitHub.

### 3. Executar o backend com H2

No mesmo terminal:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev" "-Dmaven.test.skip=true"
```

O backend utiliza:

```text
http://localhost:8080
```

O perfil `dev` configura o banco H2 com persistência local em arquivo.

Os arquivos do banco não devem ser enviados ao GitHub.

### 4. Executar o frontend

Abra outro terminal na raiz do projeto:

```powershell
cd frontend
npm ci
npm start
```

Acesse:

```text
http://localhost:3000
```

O backend precisa estar em execução para utilizar as funcionalidades de autenticação e gerenciamento de dados.

### 5. Criar uma conta

Na tela inicial:

1. Selecione **Criar conta**.
2. Informe nome, e-mail e senha.
3. Conclua o cadastro.
4. Entre com suas credenciais.

Novos usuários recebem automaticamente o perfil `VENDEDOR`.

---

## Configurações de Banco de Dados

O projeto possui perfis separados:

- `dev`: utiliza H2 para desenvolvimento e testes locais.
- `mysql`: utiliza MySQL e exige configuração das credenciais de conexão.

Para utilizar o H2, não é necessário instalar um servidor MySQL.

O banco H2 é local. Os dados criados em um computador não são automaticamente compartilhados com os demais integrantes.

## Observações

- Não enviar senhas, tokens JWT ou arquivos `.env` ao repositório.
- Não versionar arquivos locais do banco H2.
- O GitHub disponibiliza o código-fonte, mas não hospeda automaticamente o backend Spring Boot.
- A publicação de uma versão funcional na internet exigirá hospedagem do backend e configuração de um banco apropriado.

---

## Equipe — Squad 53

Projeto desenvolvido colaborativamente pelos integrantes do Squad 53 para o desafio da Ative OS.