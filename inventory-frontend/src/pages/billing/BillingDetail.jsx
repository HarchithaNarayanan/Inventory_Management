import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const BillingDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [bill, setBill] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBill = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/api/billings/${id}`);
        setBill(response.data.data);
      } catch (err) {
        setError('Failed to load bill details. ' + (err.response?.data?.message || err.message));
      } finally {
        setLoading(false);
      }
    };
    fetchBill();
  }, [id]);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '200px', color: 'var(--color-text-secondary)' }}>
        Loading bill details...
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <div className="alert alert-error">⚠️ {error}</div>
        <button onClick={() => navigate(-1)} style={backBtnStyle}>← Back</button>
      </div>
    );
  }

  if (!bill) return null;

  const lineItems = bill.lineItems || bill.items || bill.billingDetails || [];
  const totalAmount = bill.totalAmount ?? 0;
  const discountAmount = bill.discountAmount ?? 0;
  const taxAmount = bill.taxAmount ?? 0;
  const netAmount = bill.netAmount ?? 0;

  const hasAnyGst = lineItems.some(item => (item.gstPercent ?? item.gst ?? 0) > 0);
  const showTaxSummary = taxAmount > 0;
  const hasAnyDiscount = lineItems.some(item => (item.discountPercent ?? item.discount ?? 0) > 0);
  const showDiscountSummary = discountAmount > 0;

  return (
    <div className="page-container">
      {/* Action Bar — hidden on print */}
      <div className="no-print" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <button onClick={() => navigate(-1)} style={backBtnStyle}>← Back</button>
        <button onClick={() => window.print()} style={printBtnStyle}>🖨 Print Invoice</button>
      </div>

      {/* Invoice Card */}
      <div style={{
        background: 'var(--color-card-bg)',
        border: '1px solid var(--color-card-border)',
        borderRadius: 'var(--radius-lg)',
        boxShadow: 'var(--shadow-card)',
        overflow: 'hidden',
        maxWidth: '900px',
        margin: '0 auto',
      }}>
        {/* Invoice Header Strip */}
        <div style={{ background: 'var(--color-accent)', padding: '20px 32px', color: '#fff' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.6rem', fontWeight: '700', margin: 0, letterSpacing: '-0.3px' }}>
                SALES INVOICE
              </h2>
              <p style={{ margin: '4px 0 0', opacity: 0.85, fontSize: '0.9rem' }}>Inventory Management System</p>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '1.3rem', fontWeight: '700', fontFamily: 'var(--font-heading)' }}>
                #{bill.billNo}
              </div>
              <div style={{ fontSize: '0.9rem', opacity: 0.85, marginTop: '4px' }}>
                {bill.billDate}
              </div>
            </div>
          </div>
        </div>

        {/* Bill Info Grid */}
        <div style={{ padding: '28px 32px', borderBottom: '1px solid var(--color-card-border)' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
            <div>
              <p style={labelStyle}>Customer</p>
              <p style={valueStyle}>{bill.customerName || '—'}</p>
            </div>
            <div>
              <p style={labelStyle}>Payment Status</p>
              <span style={{
                display: 'inline-block',
                padding: '4px 12px',
                borderRadius: '4px',
                fontSize: '0.85rem',
                fontWeight: '700',
                background: bill.paymentStatus === 'PAID' ? '#dcfce7' : '#fee2e2',
                color: bill.paymentStatus === 'PAID' ? '#166534' : '#991b1b',
              }}>
                {bill.paymentStatus}
              </span>
            </div>
            {bill.remarks && (
              <div style={{ gridColumn: '1 / -1' }}>
                <p style={labelStyle}>Remarks</p>
                <p style={{ ...valueStyle, color: 'var(--color-text-secondary)' }}>{bill.remarks}</p>
              </div>
            )}
          </div>
        </div>

        {/* Line Items Table */}
        <div style={{ padding: '28px 32px', borderBottom: '1px solid var(--color-card-border)' }}>
          <h3 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)', marginBottom: '16px', fontSize: '1rem', fontWeight: '600' }}>
            Line Items
          </h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
            <thead>
              <tr style={{ background: '#f8fafc' }}>
                <th style={thStyle}>#</th>
                <th style={thStyle}>Item Name</th>
                <th style={{ ...thStyle, textAlign: 'right' }}>Qty</th>
                <th style={{ ...thStyle, textAlign: 'right' }}>Unit Price</th>
                {hasAnyDiscount && <th style={{ ...thStyle, textAlign: 'right' }}>Disc %</th>}
                {hasAnyGst && <th style={{ ...thStyle, textAlign: 'right' }}>GST %</th>}
                <th style={{ ...thStyle, textAlign: 'right' }}>Line Total</th>
              </tr>
            </thead>
            <tbody>
              {lineItems.length === 0 ? (
                <tr>
                  <td colSpan={4 + (hasAnyDiscount ? 1 : 0) + (hasAnyGst ? 1 : 0) + 1} style={{ padding: '20px', textAlign: 'center', color: 'var(--color-text-muted)' }}>
                    No line items found.
                  </td>
                </tr>
              ) : (
                lineItems.map((item, idx) => {
                  const qty = item.quantity ?? item.qty ?? 0;
                  const unitPrice = item.unitPrice ?? item.rate ?? 0;
                  const discountPct = item.discountPercent ?? item.discount ?? 0;
                  const gstPct = item.gstPercent ?? item.gst ?? 0;
                  const lineTotal = item.lineTotal ?? item.amount ?? (qty * unitPrice);
                  return (
                    <tr key={idx} style={{ borderBottom: '1px solid #f1f5f9' }}>
                      <td style={tdStyle}>{idx + 1}</td>
                      <td style={{ ...tdStyle, fontWeight: '500' }}>{item.itemName || item.name || '—'}</td>
                      <td style={{ ...tdStyle, textAlign: 'right' }}>{qty}</td>
                      <td style={{ ...tdStyle, textAlign: 'right' }}>₹{Number(unitPrice).toFixed(2)}</td>
                      {hasAnyDiscount && <td style={{ ...tdStyle, textAlign: 'right' }}>{discountPct}%</td>}
                      {hasAnyGst && <td style={{ ...tdStyle, textAlign: 'right' }}>{gstPct}%</td>}
                      <td style={{ ...tdStyle, textAlign: 'right', fontWeight: '600' }}>₹{Number(lineTotal).toFixed(2)}</td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Summary */}
        <div style={{ padding: '24px 32px 32px' }}>
          <div style={{ maxWidth: '320px', marginLeft: 'auto' }}>
            <div style={summaryRowStyle}>
              <span style={{ color: 'var(--color-text-secondary)' }}>Total Amount</span>
              <span>₹{Number(totalAmount).toFixed(2)}</span>
            </div>
            {showDiscountSummary && (
              <div style={summaryRowStyle}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Discount</span>
                <span style={{ color: 'var(--color-error)' }}>- ₹{Number(discountAmount).toFixed(2)}</span>
              </div>
            )}
            {showTaxSummary && (
              <div style={summaryRowStyle}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Tax (GST)</span>
                <span>₹{Number(taxAmount).toFixed(2)}</span>
              </div>
            )}
            <div style={{
              ...summaryRowStyle,
              borderTop: '2px solid var(--color-card-border)',
              paddingTop: '12px',
              marginTop: '8px',
              fontWeight: '700',
              fontSize: '1.1rem',
              color: 'var(--color-text-primary)',
            }}>
              <span>Net Amount</span>
              <span style={{ color: 'var(--color-accent)' }}>₹{Number(netAmount).toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Print Styles */}
      <style>{`
        @media print {
          .no-print { display: none !important; }
          body { background: #fff !important; }
          .page-container { padding: 0 !important; }
        }
      `}</style>
    </div>
  );
};

const labelStyle = {
  fontSize: '0.8rem',
  fontWeight: '600',
  color: 'var(--color-text-muted)',
  textTransform: 'uppercase',
  letterSpacing: '0.05em',
  marginBottom: '4px',
};

const valueStyle = {
  fontSize: '1rem',
  fontWeight: '600',
  color: 'var(--color-text-primary)',
  margin: 0,
};

const thStyle = {
  padding: '10px 12px',
  textAlign: 'left',
  fontWeight: '600',
  color: 'var(--color-text-secondary)',
  fontSize: '0.82rem',
  textTransform: 'uppercase',
  letterSpacing: '0.04em',
  borderBottom: '2px solid var(--color-card-border)',
};

const tdStyle = {
  padding: '10px 12px',
  color: 'var(--color-text-primary)',
};

const summaryRowStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: '6px 0',
  fontSize: '0.95rem',
};

const backBtnStyle = {
  background: 'transparent',
  border: '1px solid var(--color-card-border)',
  borderRadius: 'var(--radius-md)',
  padding: '8px 18px',
  cursor: 'pointer',
  color: 'var(--color-text-primary)',
  fontFamily: 'var(--font-body)',
  fontWeight: '600',
  fontSize: '0.9rem',
};

const printBtnStyle = {
  background: 'var(--color-accent)',
  border: 'none',
  borderRadius: 'var(--radius-md)',
  padding: '8px 18px',
  cursor: 'pointer',
  color: '#fff',
  fontFamily: 'var(--font-body)',
  fontWeight: '600',
  fontSize: '0.9rem',
};

export default BillingDetail;
