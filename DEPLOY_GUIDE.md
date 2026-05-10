# How to Run Your ATM Project on a Server (Render)

Since you have prepared your project with a `Dockerfile` and environment variables, here are the steps to get it running on a live server using **Render**.

## Step 1: Push Your Changes to GitHub
Make sure all your latest changes (including the `Dockerfile`) are pushed to your GitHub repository.
```bash
git add .
git commit -m "Add Dockerfile and deployment configs"
git push origin main
```

## Step 2: Deploy the Backend (Spring Boot)
1. Log in to [Render.com](https://render.com/).
2. Click **New +** and select **Web Service**.
3. Connect your GitHub repository.
4. Set the following settings:
   - **Name**: `atm-backend`
   - **Root Directory**: `BACKEND`
   - **Environment**: `Docker`
5. Click **Advanced** and add the following **Environment Variables**:
   - `DB_URL`: Your MySQL connection string (e.g., `jdbc:mysql://your-db-host:3306/atm_db`).
   - `DB_USERNAME`: Your database username.
   - `DB_PASSWORD`: Your database password.
   - `MAIL_FROM`: Your Gmail address.
   - `MAIL_PASSWORD`: Your Google App Password.
6. Click **Create Web Service**. Render will build the Docker image and start the server.

## Step 3: Deploy the Frontend (Vite)
1. Click **New +** and select **Static Site**.
2. Connect the same GitHub repository.
3. Set the following settings:
   - **Name**: `atm-frontend`
   - **Root Directory**: `FRONTEND`
   - **Build Command**: `npm run build`
   - **Publish Directory**: `dist`
4. Click **Advanced** and add this **Environment Variable**:
   - `VITE_API_BASE_URL`: The URL of your deployed backend (e.g., `https://atm-backend.onrender.com`).
5. Click **Create Static Site**.

## Step 4: Verify the Connection
Once both services are "Live":
1. Open your Frontend URL (e.g., `https://atm-frontend.onrender.com`).
2. Try to register or login.
3. The frontend will now communicate with your live backend server.

---

### Alternative: Run Locally with Docker
If you want to run it on your own server/machine using Docker:
1. Open a terminal in the `BACKEND` folder.
2. Build the image: `docker build -t atm-backend .`
3. Run the container:
   ```bash
   docker run -p 21279:21279 -e DB_URL=your_db_url -e DB_USERNAME=your_user atm-backend
   ```
