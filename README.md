<div align="center">

<h1>✈️ Gerenciador de Pontos e Milhas — Backend</h1>

<p>API REST para gerenciamento de pontos e milhas de usuários, desenvolvida com <strong>Java + Spring Boot</strong>.</p>

<p>
  <a href="https://gerenciador-de-milhas.vercel.app/" target="_blank">
    <img src="https://img.shields.io/badge/🚀 Deploy-Vercel-black?style=for-the-badge" alt="Deploy" />
  </a>
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
</p>

<p>
  <a href="https://github.com/ErickDevp/Projeto-Web-1-FrontEnd">🖥️ Repositório Front-end</a> •
  <a href="https://gerenciador-de-milhas.vercel.app/">🌐 Demo ao vivo</a>
</p>

</div>

---

## 📋 Sobre o Projeto

O **Gerenciador de Pontos e Milhas** é uma aplicação web fullstack desenvolvida como projeto acadêmico no **IFS (Instituto Federal de Sergipe)**. O sistema permite que usuários cadastrem e acompanhem seus programas de fidelidade, acumulem pontos e milhas de diferentes companhias aéreas e cartões de crédito, e gerenciem resgates e transferências.

Este repositório contém a **API REST (back-end)** que alimenta a aplicação.

---

## 🚀 Funcionalidades

- 🔐 **Autenticação** com JWT (login/registro de usuários)
- 👤 **Gerenciamento de usuários** com upload de foto de perfil
- 🏦 **Cadastro de programas de milhas** e saldos por cartão/companhia
- 📊 **Registro de transações** (acúmulo e resgate de pontos)
- 🔔 **Notificações de expiração** de milhas
- 🐳 **Docker** pronto para implantação em produção

---

## 🛠️ Tecnologias Utilizadas

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.x |
| Segurança | Spring Security + JWT |
| Persistência | Spring Data JPA / Hibernate |
| Banco de Dados | MySQL |
| Build | Maven |
| Containerização | Docker |
| Armazenamento | Upload local de arquivos (`/uploads`) |

---

## 📁 Estrutura do Projeto

```
src/
└── main/
    ├── java/br/edu/ifs/academico/
    │   ├── controller/      # Endpoints REST
    │   ├── entity/          # Entidades JPA
    │   │   └── enums/       # Enumerações do domínio
    │   ├── repository/      # Interfaces de acesso ao banco
    │   ├── service/         # Regras de negócio
    │   └── security/        # Configurações de autenticação JWT
    └── resources/
        └── application.properties  # Configurações da aplicação
uploads/
└── usuarios/               # Fotos de perfil dos usuários
```

---

## ⚙️ Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8.0+](https://www.mysql.com/)
- [Docker](https://www.docker.com/) *(opcional)*

---

## 🔧 Configuração e Execução

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/Academico.git
cd Academico
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp .env_example .env
```

Edite o `.env` com os valores adequados:

```env
DB_URL=jdbc:mysql://localhost:3306/gerenciador_milhas
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=seu_segredo_jwt
```

### 3. Execute a aplicação

**Via Maven:**
```bash
./mvnw spring-boot:run
```

**Via Docker:**
```bash
docker build -t gerenciador-milhas-api .
docker run -p 8080:8080 --env-file .env gerenciador-milhas-api
```

A API estará disponível em: `http://localhost:8080`

---

## 🔌 Endpoints Principais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Autenticação do usuário |
| `POST` | `/auth/register` | Cadastro de novo usuário |
| `GET` | `/usuarios/{id}` | Buscar dados do usuário |
| `PUT` | `/usuarios/{id}` | Atualizar perfil |
| `POST` | `/usuarios/{id}/foto` | Upload de foto de perfil |
| `GET` | `/milhas` | Listar programas de milhas |
| `POST` | `/milhas` | Cadastrar novo programa |
| `GET` | `/transacoes` | Listar transações |
| `POST` | `/transacoes` | Registrar transação |

> 💡 Para a documentação completa da API, importe a collection no Postman ou acesse `/swagger-ui.html` com a aplicação rodando.

---

## 🗄️ Banco de Dados

Crie o banco antes de rodar a aplicação:

```sql
CREATE DATABASE gerenciador_milhas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O Spring Boot criará as tabelas automaticamente via JPA/Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

---

## 🔗 Repositórios Relacionados

| Projeto | Link |
|---|---|
| 🖥️ Front-end (React + TypeScript) | [Projeto-Web-1-FrontEnd](https://github.com/ErickDevp/Projeto-Web-1-FrontEnd) |
| 🌐 Deploy da aplicação | [gerenciador-de-milhas.vercel.app](https://gerenciador-de-milhas.vercel.app/) |

---

## 🧪 Testes

```bash
./mvnw test
```

Os resultados dos testes são salvos em `test_output.txt` e em `target/test-classes`.

---

## 🎓 Sobre

Projeto desenvolvido para a disciplina de **Projeto Web 1** no **Instituto Federal de Sergipe (IFS)**, com foco em desenvolvimento fullstack e boas práticas de API REST.

---

<div align="center">
  Feito com ☕ Java e 💚 Spring Boot
</div>