import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const PAGE_SIZE = 8;

const PurchaseList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  const fetchPurchases = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/api/purchases/page`, {
        params: { page: page, size: 8, search: search }
      });
      setData(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch (err) {
      setError('Failed to fetch. ' + (err.response?.data?.message || ''));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      fetchPurchases();
    }, 500);
    return () => clearTimeout(delayDebounceFn);
  }, [page, search]);

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setPage(0);
  };

  if (loading) return <div>Loading purchases...</div>;
  if (error) return <div className="alert alert-error">⚠️ {error}</div>;

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>Receipt of Material (Purchases)</h1>
        <button className="btn-primary" style={{ width: 'auto', marginTop: 0 }} onClick={() => navigate('/purchases/new')}>
          + New Receipt
        </button>
      </div>

      {/* Search Bar */}
      <div style={{ position: 'relative', marginBottom: '18px' }}>
        <span style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)', fontSize: '16px', pointerEvents: 'none' }}>
          🔍
        </span>
        <input
          type="text"
          placeholder="Search by bill no or supplier..."
          value={search}
          onChange={handleSearchChange}
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
        {data.length === 0 ? (
          <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', padding: '20px' }}>
            {search ? 'No purchases match your search.' : 'No purchases found. Click "New Receipt" to create one.'}
          </p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid #e2e8f0', textAlign: 'left', color: 'var(--color-text-secondary)' }}>
                <th style={{ padding: '12px' }}>Bill No</th>
                <th style={{ padding: '12px' }}>Date</th>
                <th style={{ padding: '12px' }}>Supplier</th>
                <th style={{ padding: '12px' }}>Net Amount</th>
                <th style={{ padding: '12px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.map(purchase => (
                <tr key={purchase.purchaseId} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '12px', fontWeight: '500' }}>{purchase.billNo}</td>
                  <td style={{ padding: '12px' }}>{purchase.billDate}</td>
                  <td style={{ padding: '12px' }}>{purchase.supplierName}</td>
                  <td style={{ padding: '12px' }}>₹{purchase.netAmount.toFixed(2)}</td>
                  <td style={{ padding: '12px' }}>
                    <button
                      onClick={() => navigate(`/purchases/${purchase.purchaseId}`)}
                      style={{ background: 'transparent', border: '1px solid #00d4aa', color: '#00d4aa', padding: '4px 12px', borderRadius: '4px', cursor: 'pointer' }}>
                      View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '20px', paddingTop: '16px', borderTop: '1px solid var(--color-card-border)' }}>
            <button
              onClick={() => setPage(p => Math.max(0, p - 1))}
              disabled={page === 0}
              style={{ padding: '6px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-card-border)', background: page === 0 ? '#f1f5f9' : 'var(--color-accent)', color: page === 0 ? 'var(--color-text-muted)' : '#fff', cursor: page === 0 ? 'not-allowed' : 'pointer', fontWeight: '600', fontSize: '0.9rem' }}>
              ← Previous
            </button>
            <span style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem', fontWeight: '500' }}>
              Page {page + 1} of {totalPages}
            </span>
            <button
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              style={{ padding: '6px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-card-border)', background: page >= totalPages - 1 ? '#f1f5f9' : 'var(--color-accent)', color: page >= totalPages - 1 ? 'var(--color-text-muted)' : '#fff', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer', fontWeight: '600', fontSize: '0.9rem' }}>
              Next →
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default PurchaseList;
