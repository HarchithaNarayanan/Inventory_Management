import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const PurchaseForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [suppliers, setSuppliers] = useState([]);
  const [items, setItems] = useState([]);

  const [formData, setFormData] = useState({
    billNo: '',
    billDate: new Date().toISOString().split('T')[0],
    supplierId: '',
    remarks: '',
    taxAmount: 0,
    discountAmount: 0,
  });

  const [purchaseDetails, setPurchaseDetails] = useState([]);

  useEffect(() => {
    fetchSuppliers();
    fetchItems();
  }, []);

  const fetchSuppliers = async () => {
    try {
      const res = await api.get('/api/suppliers');
      setSuppliers(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchItems = async () => {
    try {
      const res = await api.get('/api/items');
      setItems(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleDetailChange = (index, field, value) => {
    const newDetails = [...purchaseDetails];
    newDetails[index][field] = value;
    
    if (field === 'itemId') {
      const selectedItem = items.find(i => i.itemId.toString() === value.toString());
      if (selectedItem) {
        newDetails[index].unitPrice = selectedItem.purchaseRate || 0;
        newDetails[index].gstPercent = selectedItem.gstPercent || 0;
      }
    }
    
    setPurchaseDetails(newDetails);
  };

  const addLineItem = () => {
    setPurchaseDetails([...purchaseDetails, { itemId: '', quantity: 1, unitPrice: 0, gstPercent: 0 }]);
  };

  const removeLineItem = (index) => {
    const newDetails = purchaseDetails.filter((_, i) => i !== index);
    setPurchaseDetails(newDetails);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (purchaseDetails.length === 0) {
      setError('Please add at least one item to the receipt.');
      setLoading(false);
      return;
    }

    try {
      const payload = {
        ...formData,
        purchaseDetails: purchaseDetails.map(d => ({
          itemId: Number(d.itemId),
          quantity: Number(d.quantity),
          unitPrice: Number(d.unitPrice),
          gstPercent: Number(d.gstPercent)
        }))
      };
      
      await api.post('/api/purchases', payload);
      navigate('/purchases');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save receipt');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>New Receipt of Material</h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '20px' }}>⚠️ {error}</div>}

      <form onSubmit={handleSubmit} className="form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '24px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '20px', marginBottom: '24px' }}>
          <div className="form-group">
            <label>Bill/Receipt Number</label>
            <input type="text" className="form-control" value={formData.billNo} onChange={e => setFormData({...formData, billNo: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Bill Date</label>
            <input type="date" className="form-control" value={formData.billDate} onChange={e => setFormData({...formData, billDate: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Supplier</label>
            <select className="form-control" value={formData.supplierId} onChange={e => setFormData({...formData, supplierId: e.target.value})} required>
              <option value="">Select Supplier...</option>
              {suppliers.map(s => <option key={s.supplierId} value={s.supplierId}>{s.supplierName}</option>)}
            </select>
          </div>
        </div>

        <h3 style={{ marginBottom: '16px', borderBottom: '1px solid #e2e8f0', paddingBottom: '8px' }}>Line Items</h3>
        <table style={{ width: '100%', marginBottom: '24px', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ textAlign: 'left', background: '#f8fafc' }}>
              <th style={{ padding: '8px' }}>Item</th>
              <th style={{ padding: '8px' }}>Qty</th>
              <th style={{ padding: '8px' }}>Unit Price (₹)</th>
              <th style={{ padding: '8px' }}>GST %</th>
              <th style={{ padding: '8px' }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {purchaseDetails.map((detail, index) => (
              <tr key={index}>
                <td style={{ padding: '8px' }}>
                  <select className="form-control" value={detail.itemId} onChange={e => handleDetailChange(index, 'itemId', e.target.value)} required>
                    <option value="">Select Item...</option>
                    {items.map(i => <option key={i.itemId} value={i.itemId}>{i.itemName}</option>)}
                  </select>
                </td>
                <td style={{ padding: '8px' }}>
                  <input type="number" className="form-control" value={detail.quantity} onChange={e => handleDetailChange(index, 'quantity', e.target.value)} min="0.01" step="0.01" required />
                </td>
                <td style={{ padding: '8px' }}>
                  <input type="number" className="form-control" value={detail.unitPrice} onChange={e => handleDetailChange(index, 'unitPrice', e.target.value)} min="0" step="0.01" required />
                </td>
                <td style={{ padding: '8px' }}>
                  <input type="number" className="form-control" value={detail.gstPercent} onChange={e => handleDetailChange(index, 'gstPercent', e.target.value)} min="0" step="0.01" />
                </td>
                <td style={{ padding: '8px' }}>
                  <button type="button" onClick={() => removeLineItem(index)} style={{ color: 'red', background: 'transparent', border: 'none', cursor: 'pointer' }}>✖</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div style={{ marginBottom: '24px' }}>
          <button type="button" className="btn-secondary" onClick={addLineItem} style={{ border: '1px solid var(--color-text-secondary)', background: 'transparent', padding: '8px 16px', borderRadius: '4px', cursor: 'pointer' }}>
            + Add Item
          </button>
        </div>

        <div className="form-group" style={{ marginBottom: '24px' }}>
          <label>Remarks</label>
          <input type="text" className="form-control" value={formData.remarks} onChange={e => setFormData({...formData, remarks: e.target.value})} />
        </div>

        <div className="form-actions" style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
          <button type="button" className="btn-secondary" onClick={() => navigate('/purchases')} disabled={loading}>Cancel</button>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Saving...' : 'Save Receipt'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PurchaseForm;
