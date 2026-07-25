import React, { useState, useEffect } from 'react';
import api from '../../api/axiosConfig';

const roleBadgeStyle = (role) => {
  const base = {
    display: 'inline-block',
    padding: '3px 10px',
    borderRadius: '12px',
    fontSize: '12px',
    fontWeight: '600',
    letterSpacing: '0.5px',
  };
  switch (role) {
    case 'ROLE_ADMIN':
      return { ...base, background: '#ede9fe', color: '#7c3aed' };
    case 'ROLE_MANAGER':
      return { ...base, background: '#dbeafe', color: '#1d4ed8' };
    case 'ROLE_STAFF':
      return { ...base, background: '#dcfce7', color: '#15803d' };
    default:
      return { ...base, background: '#f1f5f9', color: '#475569' };
  }
};

const statusBadgeStyle = (active) => ({
  display: 'inline-block',
  padding: '3px 10px',
  borderRadius: '12px',
  fontSize: '12px',
  fontWeight: '600',
  background: active ? '#dcfce7' : '#fee2e2',
  color: active ? '#15803d' : '#b91c1c',
});

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/auth/users');
      setUsers(res.data.data || res.data || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  const filteredUsers = users.filter((u) => {
    const q = searchQuery.toLowerCase();
    return (
      (u.employeeName || '').toLowerCase().includes(q) ||
      (u.username || '').toLowerCase().includes(q)
    );
  });

  const formatRole = (role) => role?.replace('ROLE_', '') || role;

  if (loading) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: 'var(--color-text-secondary)' }}>
        Loading Users...
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)', marginBottom: '4px' }}>
            User Management
          </h1>
          <p style={{ color: 'var(--color-text-secondary)', margin: 0 }}>
            View and manage all system users.
          </p>
        </div>
      </div>

      {error && (
        <div className="alert alert-error" style={{ marginBottom: '16px' }}>⚠️ {error}</div>
      )}

      {/* Search Bar */}
      <div
        style={{
          background: 'var(--color-card-bg)',
          border: '1px solid var(--color-card-border)',
          borderRadius: 'var(--radius-lg)',
          padding: '16px 24px',
          marginBottom: '24px',
          boxShadow: 'var(--shadow-card)',
        }}
      >
        <input
          type="text"
          className="form-control"
          placeholder="🔍 Search by name or username..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{ maxWidth: '400px' }}
        />
      </div>

      {/* Users Table */}
      <div
        style={{
          background: 'var(--color-card-bg)',
          border: '1px solid var(--color-card-border)',
          borderRadius: 'var(--radius-lg)',
          boxShadow: 'var(--shadow-card)',
          overflow: 'hidden',
        }}
      >
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr
              style={{
                background: 'var(--color-table-header-bg)',
                borderBottom: '2px solid var(--color-card-border)',
                textAlign: 'left',
              }}
            >
              <th style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontWeight: '600', fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                #
              </th>
              <th style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontWeight: '600', fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                Employee Name
              </th>
              <th style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontWeight: '600', fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                Username
              </th>
              <th style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontWeight: '600', fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                Role
              </th>
              <th style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontWeight: '600', fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                Status
              </th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: 'var(--color-text-secondary)' }}>
                  {searchQuery ? 'No users match your search.' : 'No users found.'}
                </td>
              </tr>
            ) : (
              filteredUsers.map((u, index) => (
                <tr
                  key={u.userId || u.id || index}
                  style={{
                    borderBottom: '1px solid var(--color-card-border)',
                    transition: 'background 0.15s',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = 'var(--color-table-row-hover)')}
                  onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
                >
                  <td style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontSize: '14px' }}>
                    {index + 1}
                  </td>
                  <td style={{ padding: '14px 20px', fontWeight: '600', color: 'var(--color-text-primary)', fontSize: '14px' }}>
                    {u.employeeName || '—'}
                  </td>
                  <td style={{ padding: '14px 20px', color: 'var(--color-text-secondary)', fontSize: '14px', fontFamily: 'monospace' }}>
                    {u.username}
                  </td>
                  <td style={{ padding: '14px 20px' }}>
                    <span style={roleBadgeStyle(u.role)}>
                      {formatRole(u.role)}
                    </span>
                  </td>
                  <td style={{ padding: '14px 20px' }}>
                    <span style={statusBadgeStyle(u.active !== false)}>
                      {u.active !== false ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {filteredUsers.length > 0 && (
          <div
            style={{
              padding: '12px 20px',
              color: 'var(--color-text-secondary)',
              fontSize: '13px',
              borderTop: '1px solid var(--color-card-border)',
            }}
          >
            Showing {filteredUsers.length} of {users.length} user{users.length !== 1 ? 's' : ''}
          </div>
        )}
      </div>
    </div>
  );
};

export default UserList;
