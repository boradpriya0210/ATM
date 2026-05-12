
# 🏛️ Aura Bank - Secure Digital ATM Interface

Aura Bank is a modern, full-stack digital ATM application built with a premium glassmorphic design and robust security features. It provides a seamless banking experience including account registration, secure 2FA authentication, and transaction management.

![Aura Bank UI](https://raw.githubusercontent.com/boradpriya0210/ATM/main/FRONTEND/public/preview.png) *(Placeholder for your preview image)*

## ✨ Key Features

- **🔐 Secure Authentication**: Multi-layered security with Account Number and 4-digit PIN.
- **📧 2FA Verification**: Email-based One-Time Password (OTP) for sensitive operations.
- **💰 Transaction Management**: Easily deposit and withdraw funds with real-time balance updates.
- **📜 Transaction History**: Track your financial activities with a detailed transaction log.
- **🎨 Premium UI/UX**: Modern glassmorphic interface with smooth animations and responsive design.
- **🛠️ Robust Backend**: Powered by Spring Boot with MySQL for reliable data persistence.

## 🚀 Tech Stack

### Frontend
- **HTML5 & CSS3**: Custom vanilla CSS with glassmorphism effects.
- **JavaScript**: Modern ES6+ logic.
- **Vite**: Ultra-fast frontend build tool.
- **Google Fonts**: Outfit & JetBrains Mono for a premium look.

### Backend
- **Java 21**: Leveraging the latest Java features.
- **Spring Boot 3.2.5**: Core framework for REST APIs.
- **MySQL**: Relational database for storing user accounts and transactions.
- **BCrypt**: Password hashing for secure storage.
- **JavaMail API**: Sending OTPs for two-factor authentication.

## 🛠️ Installation & Setup

### Prerequisites
- JDK 21 or higher
- Node.js & npm
- MySQL Server

### 1. Clone the Repository
```bash
git clone https://github.com/boradpriya0210/ATM.git
cd ATM
```

### 2. Database Setup
1. Create a MySQL database named `atm_db`.
2. Run the `schema.sql` located in `BACKEND/schema.sql` to initialize tables.
3. Update `BACKEND/src/main/resources/application.properties` with your database credentials.

### 3. Backend Setup
```bash
cd BACKEND
# Build the project
mvn clean install
# Run the application
mvn spring-boot:run
```
The backend will start at `http://localhost:21279`.

### 4. Frontend Setup
```bash
cd ../FRONTEND
# Install dependencies
npm install
# Run in development mode
npm run dev
```
The frontend will be available at `http://localhost:5173`.

## 🌐 Deployment

This project is configured for deployment on **Render**.

- **Backend**: Uses a `Dockerfile` and `Procfile` for containerized deployment.
- **Frontend**: Can be deployed as a Static Site.


