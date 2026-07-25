import React, { createContext, useContext, useState, useCallback } from 'react';
import api from '../api/axiosConfig';

/**
 * AuthContext.jsx — Global authentication state provider.
 *
 * WHAT IS REACT CONTEXT?
 *   Context is React's built-in solution for sharing state that many components
 *   need, without passing props through every level of the tree (prop drilling).
 *
 *   Without Context:
 *     App → Layout → Sidebar → UserMenu (must receive "user" as prop at every level)
 *
 *   With Context:
 *     UserMenu calls useAuth() and directly accesses "user" — no prop drilling.
 *
 * WHAT THIS CONTEXT PROVIDES:
 *   - user          : { emailId, role, fullName } or null
 *   - token         : JWT string or null
 *   - isAuthenticated : boolean — true if token exists
 *   - loading       : true while checking stored token on mount
 *   - login(creds)  : calls POST /api/auth/login, stores token, sets user
 *   - logout()      : clears localStorage, resets state, redirects to /login
 *   - register(data): calls POST /api/auth/register, stores token, sets user
 */

// Step 1: Create the Context object.
// AuthContext is the "container" — it doesn't hold state, it just defines the shape.
// The actual state lives in AuthProvider's useState hooks.
const AuthContext = createContext(null);

/**
 * AuthProvider — wraps the entire app (in App.jsx) to provide auth state.
 *
 * HOW IT WORKS:
 *   <AuthProvider>           ← manages token/user state
 *     <App />               ← all children can call useAuth()
 *   </AuthProvider>
 *
 * @param {React.ReactNode} children — all child components to wrap
 */
export const AuthProvider = ({ children }) => {

  // ----------------------------------------------------------------
  // STATE — initialized from localStorage so state survives page refresh
  // ----------------------------------------------------------------

  /**
   * token: The JWT string from the last successful login.
   * Initialized from localStorage so it persists across page refreshes.
   * If no token is stored, starts as null (user is not logged in).
   */
  const [token, setToken] = useState(() => localStorage.getItem('ims_token'));

  /**
   * user: The authenticated user's info object.
   * Initialized from localStorage to avoid losing user info on refresh.
   * Stored as a JSON string, parsed back to an object here.
   */
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('ims_user');
    return stored ? JSON.parse(stored) : null;
  });

  // ----------------------------------------------------------------
  // AUTH ACTIONS
  // ----------------------------------------------------------------

  /**
   * login — Sends credentials to the API and stores the returned token.
   *
   * useCallback is used so this function reference stays stable across renders.
   * Without useCallback, every re-render creates a new function reference,
   * which could cause child components to unnecessarily re-render.
   *
   * @param {Object} credentials - { emailId, password }
   * @returns {Promise<Object>} the auth response data (token, role, etc.)
   * @throws error if credentials are wrong (component handles the error display)
   */
  const login = useCallback(async (credentials) => {
    // Call the login API — axiosConfig interceptor does NOT add token here
    // because localStorage is empty (not logged in yet)
    const response = await api.post('/api/auth/login', credentials);
    const data = response.data.data; // unwrap ApiResponse<AuthResponseDto>

    // Extract the token and user info from the response
    const { token: newToken, emailId, role, employeeName } = data;
    const userInfo = { emailId, role, employeeName };

    // Store in localStorage — persists through page refresh
    localStorage.setItem('ims_token', newToken);
    localStorage.setItem('ims_user', JSON.stringify(userInfo));

    // Update React state — triggers re-render of all consumers
    setToken(newToken);
    setUser(userInfo);

    return data; // return so the LoginPage can navigate after login
  }, []);

  /**
   * register — Registers a new user and auto-logs them in.
   *
   * @param {Object} userData - { emailId, password, fullName, role }
   * @returns {Promise<Object>} the auth response data
   */
  const register = useCallback(async (userData) => {
    const response = await api.post('/api/auth/register', userData);
    const data = response.data.data; // unwrap ApiResponse<AuthResponseDto>

    const { token: newToken, emailId, role, employeeName } = data;
    const userInfo = { emailId, role, employeeName };

    localStorage.setItem('ims_token', newToken);
    localStorage.setItem('ims_user', JSON.stringify(userInfo));

    setToken(newToken);
    setUser(userInfo);

    return data;
  }, []);

  /**
   * logout — Clears all auth state and redirects to login.
   *
   * useCallback ensures stable reference — important for components
   * that have logout in their dependency arrays (e.g., useEffect).
   */
  const logout = useCallback(() => {
    // Remove from localStorage
    localStorage.removeItem('ims_token');
    localStorage.removeItem('ims_user');

    // Clear React state
    setToken(null);
    setUser(null);

    // Redirect to login page
    window.location.href = '/login';
  }, []);

  // ----------------------------------------------------------------
  // COMPUTED VALUES
  // ----------------------------------------------------------------

  /** true if a token exists in state (user is logged in) */
  const isAuthenticated = !!token;

  /** true if user has ROLE_ADMIN */
  const isAdmin = user?.role === 'ROLE_ADMIN';

  // ----------------------------------------------------------------
  // CONTEXT VALUE — everything provided to child components
  // ----------------------------------------------------------------
  const contextValue = {
    user,            // { emailId, employeeName, role } or null
    token,           // JWT string or null
    isAuthenticated, // boolean
    isAdmin,         // boolean — true if ROLE_ADMIN
    login,           // function(credentials) → Promise
    logout,          // function() → void
    register,        // function(userData) → Promise
  };

  // Return the Provider with contextValue accessible to all children
  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

/**
 * useAuth — Custom hook to access the AuthContext.
 *
 * WHY A CUSTOM HOOK instead of importing AuthContext directly?
 *   1. Cleaner syntax: `const { user, login } = useAuth()` vs
 *      `const { user, login } = useContext(AuthContext)`
 *   2. Built-in guard: throws a helpful error if used outside <AuthProvider>
 *   3. Single import: components import useAuth, not AuthContext + useContext
 *
 * USAGE in any component inside <AuthProvider>:
 *   const { user, isAuthenticated, login, logout } = useAuth();
 *
 * @returns {Object} the full AuthContext value
 * @throws {Error} if called outside of AuthProvider
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider component');
  }
  return context;
};

export default AuthContext;
