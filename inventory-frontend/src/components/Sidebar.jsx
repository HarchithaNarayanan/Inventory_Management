import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Sidebar.css';

const Sidebar = () => {
  const { user, logout } = useAuth();
  
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isAdminOrManager = isAdmin || user?.role === 'ROLE_MANAGER';


  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <img src="/logo.jpg" alt="Logo" style={{ width: '32px', height: '32px', objectFit: 'contain', marginRight: '10px' }} />
        <h2 className="sidebar-brand">Inventory Management</h2>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
          <span className="nav-icon">📊</span>
          Dashboard
        </NavLink>

        {isAdminOrManager && (
          <NavLink to="/suppliers" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
            <span className="nav-icon">🏢</span>
            Suppliers
          </NavLink>
        )}

        {isAdminOrManager && (
          <NavLink to="/customers" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
            <span className="nav-icon">👥</span>
            Customers
          </NavLink>
        )}

        <NavLink to="/items" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
          <span className="nav-icon">🛒</span>
          Items
        </NavLink>

        {isAdmin && (
          <>
            <NavLink to="/banks" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
              <span className="nav-icon">🏦</span>
              Banks
            </NavLink>

            <NavLink to="/companies" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
              <span className="nav-icon">🏢</span>
              Companies
            </NavLink>

            <NavLink to="/users" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
              <span className="nav-icon">👤</span>
              User Management
            </NavLink>
          </>
        )}

        <div className="nav-divider" style={{ borderTop: '1px solid #334155', margin: '16px 0' }}></div>

        {isAdminOrManager && (
          <NavLink to="/purchases" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
            <span className="nav-icon">📥</span>
            Purchases
          </NavLink>
        )}

        <NavLink to="/billing" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
          <span className="nav-icon">📤</span>
          Sales
        </NavLink>

        <NavLink to="/transactions" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
          <span className="nav-icon">💸</span>
          Transactions
        </NavLink>

        <div className="nav-divider" style={{ borderTop: '1px solid #334155', margin: '16px 0' }}></div>

        {isAdminOrManager && (
          <NavLink to="/reports" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
            <span className="nav-icon">📑</span>
            Reports
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-name">{user?.employeeName || 'User'}</div>
          <div className="user-role">{user?.role?.replace('ROLE_', '')}</div>
        </div>
        <button onClick={logout} className="logout-btn">
          <span className="nav-icon">🚪</span>
          Sign Out
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
