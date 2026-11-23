# Quiz App Backend

This is the **Spring Boot** backend for the Quiz App platform. It provides RESTful APIs for authentication, quiz management, question handling, results tracking, and user profile management. The backend uses JWT for authentication and supports role-based access for users and admins.

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven or Gradle
- PostgreSQL (or your configured DB)
- Docker (optional, for containerization)

---

### 🔧 Installation

1. **Clone the repository**

```bash
git clone https://github.com/nureles16/quiz-backend.git
cd quiz-app-backend
```

2. **Configure the database**

Edit **application.properties**:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/quizdb
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
jwt.secret=your_jwt_secret_key


```

3. **Run the application**

```bash
./mvnw spring-boot:run
```

# 🐳 Docker
You can also build and run the backend using Docker:
```bash
# Build image
docker build -t quiz-app-backend .

# Run container
docker run -p 8080:8080 quiz-app-backend
```

# 🔐 Features
- ✅ User Authentication (JWT)

- ✅ Role-based Access (User/Admin)

- 📋 Quiz & Question Management

- 🧠 Quiz Taking and Result Storage

- 👤 User Profile & Registration

- 📊 Result History

- 📖 Swagger UI for API Docs
# 🔗 API Documentation
Once the app is running, open:

```bash
http://localhost:8080/swagger-ui/index.html

```

## 👥 Contributor

- [Almazbek uulu Nureles](https://www.linkedin.com/in/nureles-almazbekov-58bb47253/)
