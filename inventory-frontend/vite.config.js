import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

/**
 * Vite Configuration
 *
 * proxy: Forwards /api/** requests to Spring Boot on port 8080.
 * This avoids CORS issues during development because the browser
 * sees all requests going to the same origin (localhost:5173).
 *
 * Without proxy: Browser sends request to localhost:8080
 *   → Browser sees different origin → CORS check triggered
 *
 * With proxy: Browser sends request to localhost:5173/api/...
 *   → Vite dev server forwards it to localhost:8080 server-side
 *   → No cross-origin issue for the browser
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
