import axios from 'axios';

/**
 * axiosConfig.js — Pre-configured Axios instance for the Inventory Management API.
 *
 * WHY A CUSTOM INSTANCE instead of bare axios?
 *   - Sets baseURL once so all components use relative paths (/api/...)
 *   - Adds JWT token to EVERY request via request interceptor
 *   - Handles 401 token expiry globally via response interceptor
 *   - Sets Content-Type once — every request sends JSON
 *
 * USAGE in components:
 *   import api from '../api/axiosConfig';
 *   const response = await api.get('/api/suppliers');
 *   // → automatically sends: GET http://localhost:8080/api/suppliers
 *   //                        Authorization: Bearer eyJhbGci...
 */

// Create a custom Axios instance with default configuration.
// All components import and use 'api' instead of the bare 'axios' object.
const api = axios.create({
  // baseURL is empty because Vite's proxy handles /api/* forwarding to localhost:8080
  // This means api.get('/api/suppliers') → localhost:5173/api/suppliers → proxied to :8080
  baseURL: '',

  // All requests and responses use JSON
  headers: {
    'Content-Type': 'application/json',
  },

  // Request timeout: 10 seconds
  // If Spring Boot doesn't respond in 10s, Axios throws a network error
  timeout: 10000,
});

// ================================================================
// REQUEST INTERCEPTOR — runs BEFORE every outgoing API call
// ================================================================
/**
 * Attaches the JWT token to every request automatically.
 *
 * Flow:
 *   Component calls api.get('/api/items')
 *       → this interceptor runs
 *       → reads token from localStorage
 *       → if token exists: adds 'Authorization: Bearer <token>' header
 *       → request continues to the server
 *
 * localStorage key: 'ims_token'
 * (prefixed with 'ims_' to avoid collision with other apps on localhost)
 */
api.interceptors.request.use(
  (config) => {
    // Read the JWT token stored after login
    const token = localStorage.getItem('ims_token');

    if (token) {
      // Attach it to the Authorization header
      // Spring Security's JwtAuthFilter reads this header on every request
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    return config; // MUST return config to continue the request
  },
  (error) => {
    // Request setup failed (e.g., network offline before request was sent)
    return Promise.reject(error);
  }
);

// ================================================================
// RESPONSE INTERCEPTOR — runs AFTER every API response
// ================================================================
/**
 * Handles global error cases, especially token expiry.
 *
 * Flow on 401 Unauthorized response:
 *   Server returns 401 (token expired or invalid)
 *       → this interceptor catches it
 *       → clears stored auth data from localStorage
 *       → redirects to /login
 *       → user must log in again with fresh credentials
 *
 * Flow on other errors (404, 500, etc.):
 *   Passes the error through so individual components can handle it
 *   and show appropriate error messages.
 */
api.interceptors.response.use(
  (response) => {
    // Successful response (2xx) — pass through unchanged
    return response;
  },
  (error) => {
    if (error.response) {
      const status = error.response.status;

      if (status === 401) {
        // Token expired, invalid, or missing — force logout
        console.warn('[API] 401 Unauthorized — clearing session and redirecting to login');

        // Clear all auth data from localStorage
        localStorage.removeItem('ims_token');
        localStorage.removeItem('ims_user');

        // Redirect to login page
        // We use window.location.href (not React Router navigate) because
        // this interceptor is outside React's component tree — we don't
        // have access to useNavigate() here.
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
      }

      // 403 Forbidden — component will handle this (show "Access Denied" message)
      // 404, 409, 500 — component will handle these individually
    }

    // Always reject so the calling component's catch() block runs
    return Promise.reject(error);
  }
);

export default api;
