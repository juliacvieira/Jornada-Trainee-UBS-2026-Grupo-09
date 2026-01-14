Readme:# 📋 Expense Manager - UBS Trainee Journey 2026

Repository for the project developed by Group 9 during the UBS Trainee Journey 2026 selection process.

---

## 🚀 How to Run the Project

### ✅ Prerequisites

- **Java 17+**
- **Node.js 18+** e **npm**
- **PostgreSQL 15+**
- **Docker** (opcional, para executar PostgreSQL em container)

---

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/seu-usuario/Jornada-Trainee-UBS-2026-Grupo-09.git
cd Jornada-Trainee-UBS-2026-Grupo-09
```

### 2. Configure the Database

#### Option A: Local PostgreSQL

```bash
# Create a database
createdb expense_manager

# Configure credentials in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_manager
spring.datasource.username=your_user
spring.datasource.password=your_password
```

#### Option B: Docker

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

## 🔧 Run Backend (Spring Boot)

```bash
# From project root
./mvnw spring-boot:run

# Or with Maven installed
mvn spring-boot:run
```

**Expected Result:**

```
Started ExpenseManagerApplication in 5.493 seconds
Tomcat started on port(s): 8080 (http)
```

Backend available at: **http://localhost:8080**

---

## 🎨 Run Frontend (React + Vite)

```bash
# From project root (or frontend folder if exists)
npm install
npm run dev
```

**Expected Result:**

```
VITE v6.4.1  ready in 777 ms

➜  Local:   http://localhost:5173/
```

Frontend available at: **http://localhost:5173**

---

## 🔌 Frontend-Backend Communication

The frontend is configured to communicate with the backend through a proxy:

- Requests to `/api/*` are automatically forwarded to `http://localhost:8080`
- Configuration in `vite.config.ts`

---

## 🧪 Running Tests

### Backend Tests

```bash
./mvnw test

# Specific tests
./mvnw test -Dtest=ExpenseServiceTest
```

**Expected Result:**

```
Tests run: 11, Failures: 0, Errors: 0
```

---

## 📁 Project Structure

### Complete Directory Tree

```
Jornada-Trainee-UBS-2026-Grupo-09/
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/ubs/expensemanager/
│   │   │   ├── 📂 controller/           # REST API Controllers
│   │   │   ├── 📂 service/              # Business Logic
│   │   │   ├── 📂 repository/           # Data Access Layer (JPARepository)
│   │   │   ├── 📂 domain/               # JPA Entities
│   │   │   │   └── 📂 enums/            # Enum classes
│   │   │   ├── 📂 dto/                  # Data Transfer Objects
│   │   │   │   ├── 📂 expense/
│   │   │   │   ├── 📂 category/
│   │   │   │   ├── 📂 auth/
│   │   │   │   ├── 📂 alert/
│   │   │   │   ├── 📂 department/
│   │   │   │   ├── 📂 report/
│   │   │   │   └── 📂 employee/
│   │   │   ├── 📂 mapper/               # Entity Mappers
│   │   │   ├── 📂 config/               # Spring Configuration
│   │   │   ├── 📂 handler/              # Exception Handlers
│   │   │   ├── 📂 exception/            # Custom Exceptions
│   │   │   ├── 📂 security/             # Security Configuration
│   │   │   └── ExpenseManagerApplication.java
│   │   └── 📂 resources/
│   │       ├── application.properties   # Spring Configuration
│   │       └── 📂 db/migration/         # Flyway Scripts (V1..V11)
│   │
│   ├── 📂 app/ (Frontend - React + TypeScript)
│   │   ├── 📂 auth/                     # Authentication
│   │   │   ├── AuthContext.tsx
│   │   │   ├── AuthProvider.tsx
│   │   │   └── types.ts
│   │   ├── 📂 components/               # React Components
│   │   │   ├── Navbar.tsx
│   │   │   ├── Navigation.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── 📂 figma/                # Figma Components
│   │   │   └── 📂 ui/                   # UI Components (shadcn/ui)
│   │   │       ├── accordion.tsx
│   │   │       ├── alert-dialog.tsx
│   │   │       ├── alert.tsx
│   │   │       ├── aspect-ratio.tsx
│   │   │       ├── avatar.tsx
│   │   │       ├── badge.tsx
│   │   │       ├── button.tsx
│   │   │       ├── card.tsx
│   │   │       ├── carousel.tsx
│   │   │       ├── chart.tsx
│   │   │       ├── checkbox.tsx
│   │   │       ├── dialog.tsx
│   │   │       ├── dropdown-menu.tsx
│   │   │       ├── input.tsx
│   │   │       ├── table.tsx
│   │   │       ├── tabs.tsx
│   │   │       ├── calendar.tsx
│   │   │       ├── command.tsx
│   │   │       ├── context-menu.tsx
│   │   │       ├── collapsible.tsx
│   │   │       └── ... (more UI components)
│   │   ├── 📂 pages/                    # Application Pages
│   │   │   ├── AlertsPage.tsx
│   │   │   ├── ApprovalPage.tsx
│   │   │   ├── EmployeesPage.tsx
│   │   │   ├── ExpensesPage.tsx
│   │   │   ├── LoginPage.tsx
│   │   │   └── ReportsPage.tsx
│   │   ├── 📂 services/                 # HTTP Services
│   │   │   ├── alertService.ts
│   │   │   ├── apiClient.ts
│   │   │   ├── authService.ts
│   │   │   ├── categoryService.ts
│   │   │   ├── employeeService.ts
│   │   │   └── expenseService.ts
│   │   ├── 📂 hooks/                    # React Hooks
│   │   │   └── useAuth.ts
│   │   ├── 📂 layouts/                  # Layouts
│   │   │   └── AppLayout.tsx
│   │   ├── 📂 types/                    # TypeScript Types
│   │   │   └── index.ts
│   │   ├── 📂 routes/                   # Routes
│   │   │   └── ProtectedRoute.tsx
│   │   ├── 📂 styles/                   # CSS/Tailwind
│   │   │   ├── fonts.css
│   │   │   ├── tailwind.css
│   │   │   └── theme.css
│   │   ├── 📂 lib/                      # Utilities
│   │   │   └── date.ts
│   │   ├── 📂 assets/                   # Images and Assets
│   │   ├── App.tsx                      # Root Component
│   │   ├── main.tsx                     # React Entry Point
│   │   ├── index.css                    # Global Styles
│   │   └── translations.ts              # i18n Translations
│   │
│   └── 📂 test/
│       └── 📂 java/com/ubs/expensemanager/
│           ├── 📂 service/              # Unit Tests
│           └── 📂 integration/          # Integration Tests
│
├── 📂 dist/                         # Frontend Build Output
├── 📂 target/                       # Backend Build Output
├── 📂 public/                       # Static Files
├── 📂 receipts/                     # Expense Receipts
├── 📂 .mvn/                         # Maven Wrapper
├── 📄 pom.xml                       # Maven Dependencies (Backend)
├── 📄 package.json                  # npm Dependencies (Frontend)
├── 📄 package-lock.json
├── 📄 tsconfig.json                 # TypeScript Configuration
├── 📄 tsconfig.app.json
├── 📄 tsconfig.node.json
├── 📄 vite.config.ts                # Vite Configuration
├── 📄 tailwind.config.ts            # TailwindCSS Configuration
├── 📄 postcss.config.js             # PostCSS Configuration
├── 📄 eslint.config.js              # ESLint Configuration
├── 📄 components.json               # shadcn/ui Configuration
├── 📄 docker-compose.yml            # Docker Compose
├── 📄 mvnw                          # Maven Wrapper (macOS/Linux)
├── 📄 mvnw.cmd                      # Maven Wrapper (Windows)
├── 📄 index.html                    # React HTML Root
├── 📄 README.md                     # Project Documentation
└── 📄 LICENSE                       # MIT License
```

### Layered Architecture

#### Backend (Java - Spring Boot)

```
controller/ → service/ → repository/ → domain/
     ↓            ↓           ↓
   HTTP       Business     Database
   API        Logic        Access
```

#### Frontend (React - TypeScript)

```
pages/ → components/ → services/ → types/
  ↓         ↓            ↓
UI/UX   Components   API HTTP   Types
```

---

## 🔌 Main API Endpoints

| Method   | Endpoint                      | Description        |
| -------- | ----------------------------- | ------------------ |
| `GET`    | `/api/expenses`               | List all expenses  |
| `POST`   | `/api/expenses`               | Create new expense |
| `GET`    | `/api/expenses/{id}`          | Get expense by ID  |
| `PATCH`  | `/api/expenses/{id}`          | Update expense     |
| `DELETE` | `/api/expenses/{id}`          | Delete expense     |
| `GET`    | `/api/expenses/{id}/download` | Download receipt   |
| `GET`    | `/api/categories`             | List categories    |
| `GET`    | `/api/departments`            | List departments   |
| `GET`    | `/api/employees`              | List employees     |
| `GET`    | `/api/alerts`                 | List alerts        |

---

## 🛠️ Technologies Used

### Backend

- **Spring Boot 3.5.9** - Web framework
- **Spring Data JPA** - ORM data access
- **PostgreSQL 16.11** - Relational database
- **Flyway** - Database schema versioning
- **Lombok** - Boilerplate reduction
- **JUnit 5** - Testing framework
- **Mockito** - Mocking for tests
- **Testcontainers** - Containers for integration tests

### Frontend

- **React 18+** - UI library
- **Vite 6.4.1** - Build tool and dev server
- **TypeScript** - Static typing
- **TailwindCSS** - CSS styling
- **Axios** - HTTP client
- **React Router** - Routing

---

## 🔐 Environment Variables

Create an `.env` file in the project root (optional):

```env
# Backend - Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/expense_manager
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Frontend
VITE_API_URL=http://localhost:8080
```

---

## ✨ Main Features

- ✅ Complete expense management
- ✅ Expense categorization
- ✅ Budget control by department
- ✅ Automatic alerts for expense limits
- ✅ Receipt upload and storage (PDF, images)
- ✅ Business rule validation
- ✅ Reports dashboard
- ✅ Authentication and authorization
- ✅ Unit and integration tests
- ✅ Documented RESTful API

---

## 🐛 Troubleshooting

### Error: "psql: command not found"

PostgreSQL is not installed. Use Docker:

```bash
docker-compose up -d postgres
```

### Error: "Port 8080 is already in use"

Another application is using the port:

```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Error: "Cannot find module '@babel/helper-validator-identifier'"

Clean npm cache:

```bash
cd frontend
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Frontend cannot connect to backend

Check if both servers are running:

```bash
# Backend: http://localhost:8080/api/health
# Frontend: http://localhost:5173
```

---

## 📊 Compilation and Build

### Backend Build

```bash
./mvnw clean package -DskipTests
# Generated JAR: target/expensemanager-0.0.1-SNAPSHOT.jar
```

### Frontend Build

```bash
npm run build
# Generated files: dist/
```

---

## 🤝 Contributing

1. Create a branch for your feature: `git checkout -b feature/AmazingFeature`
2. Commit your changes: `git commit -m "Add some AmazingFeature"`
3. Push to the branch: `git push origin feature/AmazingFeature`
4. Open a Pull Request

### Before committing:

```bash
# Compile and test
./mvnw clean test

# Check lint errors
npm run lint
```

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 👥 Group 09 - UBS Trainee Journey 2026

Developed by Group 9

**Last update**: January 14, 2026
