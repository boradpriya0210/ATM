import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://atm-7pj3.onrender.com/api/atm';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add the token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('atm_token'); // Or use your state management
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data || error.message;
    return Promise.reject({ message, status: error.response?.status });
  }
);

export default api;
