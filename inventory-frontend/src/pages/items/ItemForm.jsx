import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axiosConfig';

const ItemForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);

  const [formData, setFormData] = useState({
    itemCode: '',
    itemName: '',
    description: '',
    category: '',
    unitOfMeasure: '',
    unitOfRate: '',
    grade: '',
    purchaseGl: '',
    salesGl: '',
    entryId: '',
    enteredBy: '',
    modifiedId: '',
    modifiedBy: '',
    manufactureDate: '',
    expiryDate: '',
    entryDate: '',
    purchaseRate: 0,
    sellingRate: 0,
    gstPercent: 0,
    openingStock: 0,
    reorderLevel: 0,
    status: 'ACTIVE'
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEditMode) {
      const fetchItem = async () => {
        try {
          const response = await api.get(`/api/items/${id}`);
          setFormData(response.data.data);
        } catch (err) {
          setError('Failed to load item.');
        }
      };
      fetchItem();
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
        await api.put(`/api/items/${id}`, formData);
      } else {
        await api.post('/api/items', formData);
      }
      navigate('/items');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save item.');
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>
          {isEditMode ? 'Edit Item' : 'Add New Item'}
        </h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '20px' }}>⚠️ {error}</div>}

      <div className="form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '32px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
          
          <div className="form-group">
            <label className="form-label">Item Code *</label>
            <input type="text" name="itemCode" value={formData.itemCode} onChange={handleChange} className="form-input" required disabled={isEditMode} />
          </div>

          <div className="form-group">
            <label className="form-label">Item Name *</label>
            <input type="text" name="itemName" value={formData.itemName} onChange={handleChange} className="form-input" required />
          </div>

          <div className="form-group" style={{ gridColumn: '1 / -1' }}>
            <label className="form-label">Description</label>
            <input type="text" name="description" value={formData.description} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Category</label>
            <input type="text" name="category" value={formData.category} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Unit of Measure</label>
            <input type="text" name="unitOfMeasure" value={formData.unitOfMeasure} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Unit of Rate</label>
            <input type="text" name="unitOfRate" value={formData.unitOfRate} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Grade</label>
            <input type="text" name="grade" value={formData.grade} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Purchase GL</label>
            <input type="text" name="purchaseGl" value={formData.purchaseGl} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Sales GL</label>
            <input type="text" name="salesGl" value={formData.salesGl} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Entry ID</label>
            <input type="text" name="entryId" value={formData.entryId} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Entered By</label>
            <input type="text" name="enteredBy" value={formData.enteredBy} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Modified ID</label>
            <input type="text" name="modifiedId" value={formData.modifiedId} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Modified By</label>
            <input type="text" name="modifiedBy" value={formData.modifiedBy} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Manufacture Date</label>
            <input type="date" name="manufactureDate" value={formData.manufactureDate ? formData.manufactureDate.substring(0, 10) : ''} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Expiry Date</label>
            <input type="date" name="expiryDate" value={formData.expiryDate ? formData.expiryDate.substring(0, 10) : ''} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Entry Date</label>
            <input type="date" name="entryDate" value={formData.entryDate ? formData.entryDate.substring(0, 10) : ''} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Purchase Rate (₹)</label>
            <input type="number" step="0.01" name="purchaseRate" value={formData.purchaseRate} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Selling Rate (₹)</label>
            <input type="number" step="0.01" name="sellingRate" value={formData.sellingRate} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">GST Percent (%)</label>
            <input type="number" step="0.01" name="gstPercent" value={formData.gstPercent} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Opening Stock</label>
            <input type="number" step="0.001" name="openingStock" value={formData.openingStock} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-group">
            <label className="form-label">Reorder Level</label>
            <input type="number" step="0.001" name="reorderLevel" value={formData.reorderLevel} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-actions" style={{ gridColumn: '1 / -1', display: 'flex', gap: '16px', marginTop: '16px' }}>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Item'}
            </button>
            <button type="button" className="btn-secondary" onClick={() => navigate('/items')}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ItemForm;
