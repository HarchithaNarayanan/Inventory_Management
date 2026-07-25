import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axiosConfig';

const BankForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);

  const [formData, setFormData] = useState({
    bankCode: '',
    bankName: '',
    bankBranch: '',
    accountNo: '',
    limitAmount: 0,
    glCode: '',
    status: 'ACTIVE'
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEditMode) {
      const fetchBank = async () => {
        try {
          const response = await api.get(`/api/banks/${id}`);
          setFormData(response.data.data);
        } catch (err) {
          setError('Failed to load bank.');
        }
      };
      fetchBank();
    }
  }, [id, isEditMode]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isEditMode) {
        await api.put(`/api/banks/${id}`, formData);
      } else {
        await api.post('/api/banks', formData);
      }
      navigate('/banks');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save bank.');
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>
          {isEditMode ? 'Edit Bank' : 'Add New Bank'}
        </h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '20px' }}>⚠️ {error}</div>}

      <div className="form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '32px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
          
          <div className="form-group">
            <label className="form-label">Bank Code *</label>
            <input type="text" name="bankCode" value={formData.bankCode} onChange={handleChange} className="form-input" required disabled={isEditMode} />
          </div>

          <div className="form-group">
            <label className="form-label">Bank Name *</label>
            <input type="text" name="bankName" value={formData.bankName} onChange={handleChange} className="form-input" required />
          </div>

          <div className="form-group">
            <label className="form-label">Bank Branch</label>
            <input type="text" name="bankBranch" value={formData.bankBranch} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Account Number *</label>
            <input type="text" name="accountNo" value={formData.accountNo} onChange={handleChange} className="form-input" required disabled={isEditMode} />
          </div>

          <div className="form-group">
            <label className="form-label">Limit Amount</label>
            <input type="number" step="0.01" name="limitAmount" value={formData.limitAmount} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">GL Code</label>
            <input type="text" name="glCode" value={formData.glCode} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-actions" style={{ gridColumn: '1 / -1', display: 'flex', gap: '16px', marginTop: '16px' }}>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Bank'}
            </button>
            <button type="button" className="btn-secondary" onClick={() => navigate('/banks')}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BankForm;
