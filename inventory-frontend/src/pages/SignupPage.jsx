import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * SignupPage — Clean Light Enterprise user registration form.
 */
const SignupPage = () => {
  const navigate = useNavigate();
  const { register, isAuthenticated } = useAuth();

  // If already logged in, redirect to dashboard
  if (isAuthenticated) {
    navigate('/dashboard', { replace: true });
  }

  // ----------------------------------------------------------------
  // STATE
  // ----------------------------------------------------------------
  const [formData, setFormData] = useState({
    emailId: '',
    employeeName: '',
    password: '',
    confirmPassword: '',
    role: 'ROLE_STAFF', // defaults to Staff role
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // ----------------------------------------------------------------
  // HANDLERS
  // ----------------------------------------------------------------
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }));
    }
    setError('');
  };

  const validate = () => {
    const errors = {};
    const employeeNameRegex = /^[a-zA-Z\s]+$/;

    if (!formData.employeeName.trim()) {
      errors.employeeName = 'Employee name is required';
    } else if (!employeeNameRegex.test(formData.employeeName)) {
      errors.employeeName = 'Employee Name must contain only alphabets and spaces';
    }

    if (!formData.emailId.trim()) {
      errors.emailId = 'Email Id is required';
    } else if (formData.emailId.trim().length < 3) {
      errors.emailId = 'Email Id must be at least 3 characters';
    }

    if (!formData.password) {
      errors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      errors.password = 'Password must be at least 6 characters';
    }

    if (!formData.confirmPassword) {
      errors.confirmPassword = 'Confirm password is required';
    } else if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setError('');

    try {
      await register({
        emailId: formData.emailId.trim(),
        employeeName: formData.employeeName.trim(),
        password: formData.password,
        role: formData.role,
      });

      // Navigate to dashboard after successful signup and auto-login
      navigate('/dashboard', { replace: true });
    } catch (err) {
      if (err.response) {
        const serverMessage = err.response.data?.message;
        setError(serverMessage || 'Registration failed. Try a different Email Id.');
      } else if (err.request) {
        setError('Cannot connect to server. Please check if the backend is running.');
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  // ----------------------------------------------------------------
  // RENDER
  // ----------------------------------------------------------------
  return (
    <div className="auth-page">
      <div className="auth-card">
        
        {/* Brand Logo */}
        <div className="auth-logo">
          <div className="auth-logo-icon">📦</div>
          <span className="auth-logo-text">Inventory Management</span>
        </div>

        <h1 className="auth-title">Create Account</h1>
        <p className="auth-subtitle">Register to access the Enterprise Inventory Portal</p>

        {/* Global Error Alert */}
        {error && (
          <div className="alert alert-error" role="alert" aria-live="polite">
            <span>⚠️</span>
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          
          {/* Employee Name */}
          <div className="form-group">
            <label className="form-label" htmlFor="register-employeeName">Employee Name</label>
            <div className="input-wrapper">
              <span className="input-icon">📛</span>
              <input
                id="register-employeeName"
                type="text"
                name="employeeName"
                className={`form-input ${fieldErrors.employeeName ? 'error' : ''}`}
                placeholder="John Doe"
                value={formData.employeeName}
                onChange={handleChange}
                disabled={loading}
                aria-describedby={fieldErrors.employeeName ? 'employeeName-error' : undefined}
                autoFocus
              />
            </div>
            {fieldErrors.employeeName && (
              <p id="employeeName-error" className="field-error">⚠ {fieldErrors.employeeName}</p>
            )}
          </div>

          {/* Email Id */}
          <div className="form-group">
            <label className="form-label" htmlFor="register-emailId">Email Id</label>
            <div className="input-wrapper">
              <span className="input-icon">✉️</span>
              <input
                id="register-emailId"
                type="email"
                name="emailId"
                className={`form-input ${fieldErrors.emailId ? 'error' : ''}`}
                placeholder="john@example.com"
                value={formData.emailId}
                onChange={handleChange}
                disabled={loading}
                aria-describedby={fieldErrors.emailId ? 'emailId-error' : undefined}
              />
            </div>
            {fieldErrors.emailId && (
              <p id="emailId-error" className="field-error">⚠ {fieldErrors.emailId}</p>
            )}
          </div>

          {/* Role Dropdown */}
          <div className="form-group">
            <label className="form-label" htmlFor="register-role">System Role</label>
            <div className="input-wrapper">
              <span className="input-icon">🛠️</span>
              <select
                id="register-role"
                name="role"
                className="form-input"
                value={formData.role}
                onChange={handleChange}
                disabled={loading}
              >
                <option value="ROLE_ADMIN">Admin (All Modules)</option>
                <option value="ROLE_MANAGER">Manager (Supplier, Customer, Item, Billing, Reports)</option>
                <option value="ROLE_STAFF">Staff (Item, Billing, Receipt)</option>
              </select>
            </div>
          </div>

          {/* Password */}
          <div className="form-group">
            <label className="form-label" htmlFor="register-password">Password</label>
            <div className="input-wrapper">
              <span className="input-icon">🔒</span>
              <input
                id="register-password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                className={`form-input has-right-icon ${fieldErrors.password ? 'error' : ''}`}
                placeholder="At least 6 characters"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                aria-describedby={fieldErrors.password ? 'password-error' : undefined}
              />
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
              <p id="password-error" className="field-error">⚠ {fieldErrors.password}</p>
            )}
          </div>

          {/* Confirm Password */}
          <div className="form-group">
            <label className="form-label" htmlFor="register-confirmpassword">Confirm Password</label>
            <div className="input-wrapper">
              <span className="input-icon">🔒</span>
              <input
                id="register-confirmpassword"
                type={showConfirmPassword ? 'text' : 'password'}
                name="confirmPassword"
                className={`form-input has-right-icon ${fieldErrors.confirmPassword ? 'error' : ''}`}
                placeholder="Re-enter your password"
                value={formData.confirmPassword}
                onChange={handleChange}
                disabled={loading}
                aria-describedby={fieldErrors.confirmPassword ? 'confirmpassword-error' : undefined}
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(prev => !prev)}
                aria-label={showConfirmPassword ? 'Hide password' : 'Show password'}
                tabIndex={-1}
              >
                {showConfirmPassword ? '🙈' : '👁️'}
              </button>
            </div>
            {fieldErrors.confirmPassword && (
              <p id="confirmpassword-error" className="field-error">⚠ {fieldErrors.confirmPassword}</p>
            )}
          </div>

          {/* Submit Button */}
          <button
            id="signup-submit-btn"
            type="submit"
            className="btn-primary"
            disabled={loading}
            aria-busy={loading}
          >
            {loading ? (
              <>
                <span className="spinner" aria-hidden="true" />
                Creating Account...
              </>
            ) : (
              'Sign Up'
            )}
          </button>

        </form>

        <p className="auth-link-row">
          Already have an account?{' '}
          <Link to="/login" className="auth-link">Sign In</Link>
        </p>

      </div>
    </div>
  );
};

export default SignupPage;
