import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const AccountTransactionForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [customers, setCustomers] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [banks, setBanks] = useState([]);

  const [formData, setFormData] = useState({
    transactionDate: new Date().toISOString().split('T')[0],
    transactionType: 'RECEIPT', // RECEIPT, PAYMENT, CONTRA, JOURNAL
    paymentMode: 'CASH', // CASH, CHEQUE, BANK_TRANSFER
    amount: '',
    referenceNo: '',
    remarks: '',
    customerId: '',
    supplierId: '',
    bankId: ''
  });

  useEffect(() => {
    fetchCustomers();
    fetchSuppliers();
    fetchBanks();
  }, []);

  const fetchCustomers = async () => {
    try {
      const res = await api.get('/api/customers');
      setCustomers(res.data.data);
    } catch (err) { console.error(err); }
  };

  const fetchSuppliers = async () => {
    try {
      const res = await api.get('/api/suppliers');
      setSuppliers(res.data.data);
    } catch (err) { console.error(err); }
  };

  const fetchBanks = async () => {
    try {
      const res = await api.get('/api/banks');
      setBanks(res.data.data);
    } catch (err) { console.error(err); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const payload = {
        ...formData,
        amount: Number(formData.amount),
        customerId: formData.customerId ? Number(formData.customerId) : null,
        supplierId: formData.supplierId ? Number(formData.supplierId) : null,
        bankId: formData.bankId ? Number(formData.bankId) : null,
      };
      
      await api.post('/api/account-transactions', payload);
      navigate('/transactions');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save transaction');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>New Account Transaction</h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '20px' }}>⚠️ {error}</div>}

      <form onSubmit={handleSubmit} className="form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '24px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)' }}>
        
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
          <div className="form-group">
            <label>Transaction Date</label>
            <input type="date" className="form-control" value={formData.transactionDate} onChange={e => setFormData({...formData, transactionDate: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Amount (₹)</label>
            <input type="number" className="form-control" value={formData.amount} onChange={e => setFormData({...formData, amount: e.target.value})} min="0.01" step="0.01" required />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
          <div className="form-group">
            <label>Transaction Type</label>
            <select className="form-control" value={formData.transactionType} onChange={e => setFormData({...formData, transactionType: e.target.value, customerId: '', supplierId: ''})} required>
              <option value="RECEIPT">Receipt (Money In)</option>
              <option value="PAYMENT">Payment (Money Out)</option>
              <option value="CONTRA">Contra (Bank/Cash)</option>
              <option value="JOURNAL">Journal Entry</option>
            </select>
          </div>
          <div className="form-group">
            <label>Payment Mode</label>
            <select className="form-control" value={formData.paymentMode} onChange={e => setFormData({...formData, paymentMode: e.target.value})} required>
              <option value="CASH">Cash</option>
              <option value="CHEQUE">Cheque</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
            </select>
          </div>
        </div>

        {(formData.transactionType === 'RECEIPT' || formData.transactionType === 'JOURNAL') && (
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label>Customer (Received From)</label>
            <select className="form-control" value={formData.customerId} onChange={e => setFormData({...formData, customerId: e.target.value})}>
              <option value="">Select Customer...</option>
              {customers.map(c => <option key={c.customerId} value={c.customerId}>{c.customerName}</option>)}
            </select>
          </div>
        )}

        {(formData.transactionType === 'PAYMENT' || formData.transactionType === 'JOURNAL') && (
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label>Supplier (Paid To)</label>
            <select className="form-control" value={formData.supplierId} onChange={e => setFormData({...formData, supplierId: e.target.value})}>
              <option value="">Select Supplier...</option>
              {suppliers.map(s => <option key={s.supplierId} value={s.supplierId}>{s.supplierName}</option>)}
            </select>
          </div>
        )}

        {(formData.paymentMode !== 'CASH') && (
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label>Bank Account</label>
            <select className="form-control" value={formData.bankId} onChange={e => setFormData({...formData, bankId: e.target.value})} required={formData.paymentMode !== 'CASH'}>
              <option value="">Select Bank...</option>
              {banks.map(b => <option key={b.bankId} value={b.bankId}>{b.bankName} - {b.accountNumber}</option>)}
            </select>
          </div>
        )}

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '20px', marginBottom: '24px' }}>
          <div className="form-group">
            <label>Reference No (Cheque No / UTR)</label>
            <input type="text" className="form-control" value={formData.referenceNo} onChange={e => setFormData({...formData, referenceNo: e.target.value})} />
          </div>
          <div className="form-group">
            <label>Remarks</label>
            <input type="text" className="form-control" value={formData.remarks} onChange={e => setFormData({...formData, remarks: e.target.value})} />
          </div>
        </div>

        <div className="form-actions" style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
          <button type="button" className="btn-secondary" onClick={() => navigate('/transactions')} disabled={loading}>Cancel</button>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Saving...' : 'Save Transaction'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AccountTransactionForm;
