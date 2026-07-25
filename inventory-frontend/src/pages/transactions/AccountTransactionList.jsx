import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const PAGE_SIZE = 8;

const AccountTransactionList = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const navigate = useNavigate();

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/account-transactions');
      setTransactions(response.data.data);
    } catch (err) {
      setError('Failed to fetch transactions. ' + (err.response?.data?.message || ''));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, []);

  useEffect(() => { setPage(1); }, [search]);

  if (loading) return <div>Loading transactions...</div>;
  if (error) return <div className="alert alert-error">⚠️ {error}</div>;

  const filtered = transactions.filter(txn => {
    const q = search.toLowerCase();
    return (
      (txn.description || '').toLowerCase().includes(q) ||
      (txn.transactionType || '').toLowerCase().includes(q)
    );
  });

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>Account Transactions</h1>
        <button className="btn-primary" style={{ width: 'auto', marginTop: 0 }} onClick={() => navigate('/transactions/new')}>
          + New Transaction
        </button>
      </div>

      {/* Search Bar */}
      <div style={{ position: 'relative', marginBottom: '18px' }}>
        <span style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)', fontSize: '16px', pointerEvents: 'none' }}>
          🔍
        </span>
        <input
          type="text"
          placeholder="Search by description or type..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{
            width: '100%',
            padding: '10px 14px 10px 40px',
            border: '1px solid var(--color-card-border)',
            borderRadius: 'var(--radius-md)',
            background: 'var(--color-card-bg)',
            color: 'var(--color-text-primary)',
            fontFamily: 'var(--font-body)',
            fontSize: '0.95rem',
            outline: 'none',
            boxShadow: 'var(--shadow-card)',
          }}
        />
      </div>

      <div className="table-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '24px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        {filtered.length === 0 ? (
          <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', padding: '20px' }}>
            {search ? 'No transactions match your search.' : 'No transactions found.'}
          </p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid #e2e8f0', textAlign: 'left', color: 'var(--color-text-secondary)' }}>
                <th style={{ padding: '12px' }}>Date</th>
                <th style={{ padding: '12px' }}>Type</th>
                <th style={{ padding: '12px' }}>Mode</th>
                <th style={{ padding: '12px' }}>Amount</th>
                <th style={{ padding: '12px' }}>Reference</th>
              </tr>
            </thead>
            <tbody>
              {paginated.map(txn => (
                <tr key={txn.transactionId} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '12px' }}>{txn.transactionDate}</td>
                  <td style={{ padding: '12px' }}>
                    <span style={{ padding: '4px 8px', background: txn.transactionType === 'RECEIPT' ? '#dcfce7' : (txn.transactionType === 'PAYMENT' ? '#fee2e2' : '#e0f2fe'), color: txn.transactionType === 'RECEIPT' ? '#166534' : (txn.transactionType === 'PAYMENT' ? '#991b1b' : '#0284c7'), borderRadius: '4px', fontSize: '12px', fontWeight: '600' }}>
                      {txn.transactionType}
                    </span>
                  </td>
                  <td style={{ padding: '12px' }}>{txn.paymentMode}</td>
                  <td style={{ padding: '12px', fontWeight: '500' }}>₹{txn.amount.toFixed(2)}</td>
                  <td style={{ padding: '12px', color: 'var(--color-text-secondary)' }}>{txn.referenceNo || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '20px', paddingTop: '16px', borderTop: '1px solid var(--color-card-border)' }}>
            <button
              onClick={() => setPage(p => Math.max(1, p - 1))}
              disabled={page === 1}
              style={{ padding: '6px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-card-border)', background: page === 1 ? '#f1f5f9' : 'var(--color-accent)', color: page === 1 ? 'var(--color-text-muted)' : '#fff', cursor: page === 1 ? 'not-allowed' : 'pointer', fontWeight: '600', fontSize: '0.9rem' }}>
              ← Previous
            </button>
            <span style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem', fontWeight: '500' }}>
              Page {page} of {totalPages}
            </span>
            <button
              onClick={() => setPage(p => Math.min(totalPages, p + 1))}
              disabled={page === totalPages}
              style={{ padding: '6px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-card-border)', background: page === totalPages ? '#f1f5f9' : 'var(--color-accent)', color: page === totalPages ? 'var(--color-text-muted)' : '#fff', cursor: page === totalPages ? 'not-allowed' : 'pointer', fontWeight: '600', fontSize: '0.9rem' }}>
              Next →
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AccountTransactionList;
