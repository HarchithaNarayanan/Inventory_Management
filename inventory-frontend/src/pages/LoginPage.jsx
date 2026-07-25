import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * LoginPage — Premium dark glassmorphism login form.
 *
 * COMPONENTS USED:
 *   - useState        : manages form inputs, UI states (loading, errors, showPassword)
 *   - useNavigate     : React Router v6 hook for programmatic navigation after login
 *   - Link            : React Router's <a> equivalent — no page reload on navigate
 *   - useAuth         : custom hook to access the login() function from AuthContext
 *
 * FORM FLOW:
 *   User types credentials
 *     → client-side validation (empty check)
 *     → call login(credentials) from AuthContext
 *     → AuthContext calls POST /api/auth/login via Axios
 *     → if success: navigate to /dashboard
 *     → if error: show error alert (wrong credentials, server error, etc.)
 */
const LoginPage = () => {
  // ----------------------------------------------------------------
  // HOOKS
  // ----------------------------------------------------------------

  /**
   * useNavigate — React Router v6 hook for programmatic navigation.
   * Used to redirect to /dashboard after successful login.
   * Unlike <Navigate> component, this is called inside event handlers.
   */
  const navigate = useNavigate();

  /**
   * useAuth — extracts the login() function from AuthContext.
   * login() makes the API call and stores the token.
   */
  const { login, isAuthenticated } = useAuth();

  // If already logged in, redirect to dashboard immediately
  if (isAuthenticated) {
    navigate('/dashboard', { replace: true });
  }

  // ----------------------------------------------------------------
  // STATE
  // ----------------------------------------------------------------

  /** Form input values */
  const [formData, setFormData] = useState({
    emailId: '',
    password: '',
  });

  /** Controls loading spinner on submit button */
  const [loading, setLoading] = useState(false);

  /** Error message displayed in the alert box (from API or validation) */
  const [error, setError] = useState('');

  /** Field-level validation errors */
  const [fieldErrors, setFieldErrors] = useState({});

  /** Controls password visibility (show/hide) */
  const [showPassword, setShowPassword] = useState(false);

  /** Remember me checkbox */
  const [rememberMe, setRememberMe] = useState(false);

  // ----------------------------------------------------------------
  // HANDLERS
  // ----------------------------------------------------------------

  /**
   * handleChange — updates formData state as the user types.
   *
   * [e.target.name] — computed property key.
   * The input's name attribute (e.g., "username") is used as the state key.
   * This lets one handler manage all form fields without separate handlers.
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear field error when user starts typing
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }));
    }
    setError(''); // Clear global error on any input change
  };

  /**
   * validate — client-side validation before API call.
   * Prevents unnecessary API calls for obviously empty inputs.
   * Returns true if form is valid, false if errors exist.
   */
  const validate = () => {
    const errors = {};
    if (!formData.emailId.trim()) {
      errors.emailId = 'Email Id is required';
    }
    if (!formData.password) {
      errors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      errors.password = 'Password must be at least 6 characters';
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  /**
   * handleSubmit — form submission handler.
   *
   * async/await: login() is async (makes an API call).
   * We await it so we can handle success/error synchronously after.
   *
   * e.preventDefault(): prevents the browser's default form submission
   * (which would cause a page reload). We handle submission via Axios instead.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Client-side validation first
    if (!validate()) return;

    setLoading(true);
    setError('');

    try {
      await login({
        emailId: formData.emailId.trim(),
        password: formData.password,
      });

      // Login successful — navigate to dashboard
      // replace: true removes /login from browser history
      // so pressing back doesn't return to login after logging in
      navigate('/dashboard', { replace: true });

    } catch (err) {
      // Axios error — the interceptor passed it through after handling 401
      if (err.response) {
        // Server responded with an error status (401, 400, 500, etc.)
        const serverMessage = err.response.data?.message;
        setError(serverMessage || 'Login failed. Please check your credentials.');
      } else if (err.request) {
        // Request was made but no response (server offline, network error)
        setError('Cannot connect to server. Please check if the server is running.');
      } else {
        // Something else went wrong
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      // Always runs — reset loading state whether success or error
      setLoading(false);
    }
  };

  // ----------------------------------------------------------------
  // RENDER
  // ----------------------------------------------------------------

  return (
    <div className="auth-page">
      <div className="auth-card">

        {/* ---- Logo / Brand ---- */}
        <div className="auth-logo">
          <div className="auth-logo-icon">📦</div>
          <span className="auth-logo-text">Inventory Management</span>
        </div>

        {/* ---- Heading ---- */}
        <h1 className="auth-title">Welcome back</h1>
        <p className="auth-subtitle">Sign in to your account to continue</p>

        {/* ---- Error Alert ---- */}
        {error && (
          <div className="alert alert-error" role="alert" aria-live="polite">
            <span>⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {/* ---- Login Form ---- */}
        <form onSubmit={handleSubmit} noValidate>

          {/* Email Field */}
          <div className="form-group">
            <label className="form-label" htmlFor="login-emailId">
              Email Id
            </label>
            <div className="input-wrapper">
              <span className="input-icon">✉️</span>
              <input
                id="login-emailId"
                type="email"
                name="emailId"
                className={`form-input ${fieldErrors.emailId ? 'error' : ''}`}
                placeholder="Enter your Email Id"
                value={formData.emailId}
                onChange={handleChange}
                autoComplete="email"
                autoFocus
                disabled={loading}
                aria-describedby={fieldErrors.emailId ? 'emailId-error' : undefined}
              />
            </div>
            {fieldErrors.emailId && (
              <p id="emailId-error" className="field-error">
                ⚠ {fieldErrors.emailId}
              </p>
            )}
          </div>

          {/* Password Field */}
          <div className="form-group">
            <label className="form-label" htmlFor="login-password">
              Password
            </label>
            <div className="input-wrapper">
              <span className="input-icon">🔒</span>
              <input
                id="login-password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                className={`form-input has-right-icon ${fieldErrors.password ? 'error' : ''}`}
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
                autoComplete="current-password"
                disabled={loading}
                aria-describedby={fieldErrors.password ? 'password-error' : undefined}
              />
              {/* Show / Hide password toggle */}
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(prev => !prev)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
                tabIndex={-1}
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
            {fieldErrors.password && (
              <p id="password-error" className="field-error">
                ⚠ {fieldErrors.password}
              </p>
            )}
          </div>

          {/* Remember Me */}
          <div className="form-group">
            <label className="checkbox-row">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={e => setRememberMe(e.target.checked)}
                disabled={loading}
              />
              Remember me for 7 days
            </label>
          </div>

          {/* Submit Button */}
          <button
            id="login-submit-btn"
            type="submit"
            className="btn-primary"
            disabled={loading}
            aria-busy={loading}
          >
            {loading ? (
              <>
                <span className="spinner" aria-hidden="true" />
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </button>

        </form>

        {/* ---- Link to Signup ---- */}
        <p className="auth-link-row">
          Don&apos;t have an account?{' '}
          {/*
            Link — React Router's navigation component.
            Unlike <a href="/signup">, Link does NOT reload the page.
            It uses the History API to update the URL and re-renders
            only the parts of the app that changed — much faster.
          */}
          <Link to="/signup" className="auth-link">
            Create account
          </Link>
        </p>

      </div>
    </div>
  );
};

export default LoginPage;
