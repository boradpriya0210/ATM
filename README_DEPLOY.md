# ATM Project Deployment Guide

This document explains how to deploy the ATM full-stack application.

## 1. Backend Deployment (Spring Boot)

The backend is a Spring Boot application. It has been updated to use environment variables for database and email configurations.

### Recommended Platform: [Render](https://render.com/)
1. Create a new **Web Service**.
2. Connect your GitHub repository.
3. Set the following configurations:
   - **Environment**: `Docker` (or `Java` if using Maven build)
   - **Build Command**: `mvn clean install -DskipTests` (Run this in the `BACKEND` directory)
   - **Start Command**: `java -jar BACKEND/target/ATM-Interface-1.0.jar`
   - **Port**: `9096` (or use the `$PORT` environment variable)

### Environment Variables
Set these in your deployment dashboard:
- `DB_URL`: Your production MySQL JDBC URL (e.g., `jdbc:mysql://your-db-host:3306/atm_db`)
- `DB_USERNAME`: Your database username
- `DB_PASSWORD`: Your database password
- `MAIL_FROM`: Gmail address for sending OTPs
- `MAIL_PASSWORD`: Google App Password (not your regular password)

---

## 2. Database Setup

Ensure your production MySQL database has the required tables. You can run the `BACKEND/schema.sql` script on your production database.

---

## 3. Frontend Deployment (Vite)

The frontend is a static Vite application.

### Recommended Platform: [Render Static Site](https://render.com/) or [Vercel](https://vercel.com/)
1. Create a new **Static Site**.
2. Set the following configurations:
   - **Build Command**: `npm run build` (Run this in the `FRONTEND` directory)
   - **Publish Directory**: `FRONTEND/dist`
   
### Environment Variables
- `VITE_API_BASE_URL`: The URL of your deployed backend (e.g., `https://your-backend.onrender.com/api/atm`)

---

## 4. Key Fixes Made for Deployment
- **Dynamic DB Connection**: `DBConnection.java` now prioritizes environment variables over local defaults.
- **Dynamic Email Config**: `MailConfig.java` now uses environment variables for secure credential management.
- **CORS Support**: The backend is configured to accept requests from any origin (can be restricted in production).
- **Port Management**: Backend respects the `$PORT` variable assigned by the hosting provider.
