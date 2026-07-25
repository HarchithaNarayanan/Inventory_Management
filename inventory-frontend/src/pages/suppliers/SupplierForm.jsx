import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axiosConfig';

const SupplierForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);

  const [formData, setFormData] = useState({
    supplierCode: '',
    supplierName: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    pincode: '',
    type: 'LOCAL',
    contactPerson: '',
    phoneNo: '',
    emailId: '',
    gstNo: '',
    status: 'ACTIVE'
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEditMode) {
      const fetchSupplier = async () => {
        try {
          const response = await api.get(`/api/suppliers/${id}`);
          setFormData(response.data.data);
        } catch (err) {
          setError('Failed to load supplier. ' + (err.response?.data?.message || ''));
        }
      };
      fetchSupplier();
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
        await api.put(`/api/suppliers/${id}`, formData);
      } else {
        await api.post('/api/suppliers', formData);
      }
      navigate('/suppliers');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save supplier.');
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>
          {isEditMode ? 'Edit Supplier' : 'Add New Supplier'}
        </h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '20px' }}>⚠️ {error}</div>}

      <div className="form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '32px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
          
          <div className="form-group">
            <label className="form-label">Supplier Code *</label>
            <input type="text" name="supplierCode" value={formData.supplierCode} onChange={handleChange} className="form-input" required disabled={isEditMode} />
          </div>

          <div className="form-group">
            <label className="form-label">Supplier Name *</label>
            <input type="text" name="supplierName" value={formData.supplierName} onChange={handleChange} className="form-input" required />
          </div>

          <div className="form-group">
            <label className="form-label">Contact Person</label>
            <input type="text" name="contactPerson" value={formData.contactPerson} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Phone Number</label>
            <input type="text" name="phoneNo" value={formData.phoneNo} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input type="email" name="emailId" value={formData.emailId} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">GST Number</label>
            <input type="text" name="gstNo" value={formData.gstNo} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group" style={{ gridColumn: '1 / -1' }}>
            <label className="form-label">Address Line 1</label>
            <input type="text" name="addressLine1" value={formData.addressLine1} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group" style={{ gridColumn: '1 / -1' }}>
            <label className="form-label">Address Line 2</label>
            <input type="text" name="addressLine2" value={formData.addressLine2} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">City</label>
            <input type="text" name="city" value={formData.city} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Pincode</label>
            <input type="text" name="pincode" value={formData.pincode} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Type</label>
            <select name="type" value={formData.type} onChange={handleChange} className="form-input">
              <option value="LOCAL">LOCAL</option>
              <option value="INTERSTATE">INTERSTATE</option>
              <option value="IMPORT">IMPORT</option>
            </select>
          </div>

          <div className="form-actions" style={{ gridColumn: '1 / -1', display: 'flex', gap: '16px', marginTop: '16px' }}>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Supplier'}
            </button>
            <button type="button" className="btn-secondary" onClick={() => navigate('/suppliers')}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SupplierForm;
