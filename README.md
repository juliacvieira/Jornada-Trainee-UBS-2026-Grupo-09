Readme:# 📋 Expense Manager - Jornada Trainee UBS 2026

Repositório do projeto desenvolvido pelo Grupo 9 durante o processo seletivo da Jornada de Trainee UBS 2026.

---

## 🚀 Como Executar o Projeto

### ✅ Pré-requisitos

- **Java 17+**
- **Node.js 18+** e **npm**
- **PostgreSQL 15+**
- **Docker** (opcional, para executar PostgreSQL em container)

---

## 📦 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/Jornada-Trainee-UBS-2026-Grupo-09.git
cd Jornada-Trainee-UBS-2026-Grupo-09
```

### 2. Configure o Banco de Dados

#### Opção A: PostgreSQL Local
```bash
# Crie um banco de dados
createdb expense_manager

# Configure as credenciais em application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_manager
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

#### Opção B: Docker
```bash
docker run --name postgres-expense \
  -e POSTGRES_DB=expense_manager \
  -e POSTGRES_PASSWORD=senha \
  -p 5432:5432 \
  -d postgres:16

# Com docker-compose (se disponível no projeto)
docker-compose up -d
```

---

## 🔧 Executar o Backend (Spring Boot)

```bash
# Na raiz do projeto
./mvnw spring-boot:run

# Ou com Maven instalado
mvn spring-boot:run
```

**Resultado esperado:**
```
Started ExpenseManagerApplication in 5.493 seconds
Tomcat started on port(s): 8080 (http)
```

Backend disponível em: **http://localhost:8080**

---

## 🎨 Executar o Frontend (React + Vite)

```bash
# Na raiz do projeto (ou pasta frontend se existir)
npm install
npm run dev
```

**Resultado esperado:**
```
VITE v6.4.1  ready in 777 ms

➜  Local:   http://localhost:5173/
```

Frontend disponível em: **http://localhost:5173**

---

## 🔌 Comunicação Frontend-Backend

O frontend está configurado para se comunicar com o backend através de proxy:
- Requisições para `/api/*` são automaticamente redirecionadas para `http://localhost:8080`
- Configuração em `vite.config.ts`

---

## 🧪 Executar os Testes

### Testes do Backend
```bash
./mvnw test

# Testes específicos
./mvnw test -Dtest=ExpenseServiceTest
```

**Resultado esperado:**
```
Tests run: 11, Failures: 0, Errors: 0
```

---

## 📁 Estrutura do Projeto

```
.
├── src/
│   ├── main/
│   │   ├── java/com/ubs/expensemanager/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── service/         # Lógica de negócio
│   │   │   ├── repository/      # Data Access Layer
│   │   │   ├── domain/          # Entidades JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   └── mapper/          # Mappers de entidades
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Scripts Flyway
│   ├── test/java/com/ubs/expensemanager/
│   │   ├── service/             # Testes unitários
│   │   └── integration/         # Testes de integração
│   └── index.css, main.tsx      # Frontend React
├── public/                       # Arquivos estáticos
├── pom.xml                       # Dependências Maven
├── package.json                  # Dependências npm
├── vite.config.ts                # Configuração Vite
├── docker-compose.yml            # Definição de containers
└── README.md
```

---

## 🔌 API Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/expenses` | Listar todas as despesas |
| `POST` | `/api/expenses` | Criar nova despesa |
| `GET` | `/api/expenses/{id}` | Obter despesa por ID |
| `PATCH` | `/api/expenses/{id}` | Atualizar despesa |
| `DELETE` | `/api/expenses/{id}` | Deletar despesa |
| `GET` | `/api/expenses/{id}/download` | Baixar recibo |
| `GET` | `/api/categories` | Listar categorias |
| `GET` | `/api/departments` | Listar departamentos |
| `GET` | `/api/employees` | Listar funcionários |
| `GET` | `/api/alerts` | Listar alertas |

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Spring Boot 3.5.9** - Framework web
- **Spring Data JPA** - Acesso a dados ORM
- **PostgreSQL 16.11** - Banco de dados relacional
- **Flyway** - Versionamento de schema do BD
- **Lombok** - Redução de boilerplate
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes
- **Testcontainers** - Containers para testes de integração

### Frontend
- **React 18+** - Biblioteca de UI
- **Vite 6.4.1** - Build tool e dev server
- **TypeScript** - Tipagem estática
- **TailwindCSS** - Estilização CSS
- **Axios** - HTTP client
- **React Router** - Roteamento

---

## 🔐 Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (opcional):

```env
# Backend - Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/expense_manager
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Frontend
VITE_API_URL=http://localhost:8080
```

---

## ✨ Features Principais

- ✅ Gerenciamento completo de despesas
- ✅ Categorização de gastos
- ✅ Controle orçamentário por departamento
- ✅ Alertas automáticos para limite de despesas
- ✅ Upload e armazenamento de recibos (PDF, imagens)
- ✅ Validação de regras de negócio
- ✅ Dashboard de relatórios
- ✅ Autenticação e autorização
- ✅ Testes unitários e de integração
- ✅ API RESTful documentada

---

## 🐛 Troubleshooting

### Erro: "psql: command not found"
PostgreSQL não está instalado. Use Docker:
```bash
docker-compose up -d postgres
```

### Erro: "Port 8080 is already in use"
Outra aplicação está usando a porta:
```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Erro: "Cannot find module '@babel/helper-validator-identifier'"
Limpe o cache npm:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Frontend não conecta ao backend
Verifique se ambos os servidores estão rodando:
```bash
# Backend: http://localhost:8080/api/health
# Frontend: http://localhost:5173
```

---

## 📊 Compilação e Build

### Build do Backend
```bash
./mvnw clean package -DskipTests
# Jar gerado: target/expensemanager-0.0.1-SNAPSHOT.jar
```

### Build do Frontend
```bash
npm run build
# Arquivos gerados em: dist/
```

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature: `git checkout -b feature/AmazingFeature`
2. Commit suas mudanças: `git commit -m "Add some AmazingFeature"`
3. Push para a branch: `git push origin feature/AmazingFeature`
4. Abra um Pull Request

### Antes de fazer commit:
```bash
# Compile e teste
./mvnw clean test

# Verifique erros de lint
npm run lint
```

---

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👥 Grupo 09 - Jornada Trainee UBS 2026

Desenvolvido pelo Grupo 9

**Última atualização**: 13 de janeiro de 2026
