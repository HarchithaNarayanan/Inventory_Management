import React, { useState } from 'react';
import api from '../../api/axiosConfig';
import * as XLSX from 'xlsx';

const ReportsDashboard = () => {
  const [activeTab, setActiveTab] = useState('salesLedger');
  const [fromDate, setFromDate] = useState(new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0]);
  const [toDate, setToDate] = useState(new Date().toISOString().split('T')[0]);
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Items for Stock Ledger
  const [items, setItems] = useState([]);
  const [selectedItemId, setSelectedItemId] = useState('');

  React.useEffect(() => {
    if (activeTab === 'stockLedger') {
      fetchItems();
    }
  }, [activeTab]);

  const fetchItems = async () => {
    try {
      const res = await api.get('/api/items');
      setItems(res.data.data);
      if (res.data.data.length > 0) {
        setSelectedItemId(res.data.data[0].itemId);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const generateReport = async () => {
    setLoading(true);
    setError('');
    setReportData(null);

    try {
      let endpoint = '';
      let params = { from: fromDate, to: toDate };

      switch (activeTab) {
        case 'salesLedger':
          endpoint = '/api/reports/sales-ledger';
          break;
        case 'purchaseLedger':
          endpoint = '/api/reports/purchase-ledger';
          break;
        case 'stockLedger':
          if (!selectedItemId) {
            setError('Please select an item');
            setLoading(false);
            return;
          }
          endpoint = `/api/reports/stock-ledger`;
          params = { itemId: selectedItemId, from: fromDate, to: toDate };
          break;
        case 'salesTaxLedger':
          endpoint = '/api/reports/sales-tax-ledger';
          break;
        case 'trialBalance':
          endpoint = '/api/reports/trial-balance';
          params = { asOf: toDate }; // Trial balance just needs asOf date
          break;
        case 'profitLoss':
          endpoint = '/api/reports/profit-loss';
          break;
        default:
          throw new Error('Unknown report type');
      }

      const response = await api.get(endpoint, { params });
      setReportData(response.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate report');
    } finally {
      setLoading(false);
    }
  };

  const printReport = () => {
    window.print();
  };

  const exportExcel = () => {
    if (!reportData) return;

    let rows = [];

    switch (activeTab) {
      case 'salesLedger':
        rows = (reportData.entries || []).map((e) => ({
          Date: e.billDate,
          'Bill No': e.billNo,
          Customer: e.customerName,
          'Tax Amount': e.taxAmount,
          'Net Amount': e.netAmount,
        }));
        break;
      case 'purchaseLedger':
        rows = (reportData.entries || []).map((e) => ({
          Date: e.purchaseDate,
          'Bill No': e.billNo,
          Supplier: e.supplierName,
          'Tax Amount': e.taxAmount,
          'Net Amount': e.netAmount,
        }));
        break;
      case 'stockLedger':
        rows = (reportData.entries || []).map((e) => ({
          Date: e.date,
          Reference: e.reference,
          'In Qty': e.inQty,
          'Out Qty': e.outQty,
          Balance: e.balance,
        }));
        break;
      case 'salesTaxLedger':
        rows = (reportData.entries || []).map((e) => ({
          Date: e.date,
          'Bill No': e.billNo,
          Customer: e.customerName,
          'Taxable Amount': e.taxableAmount,
          'Tax Collected': e.taxAmount,
        }));
        break;
      case 'trialBalance':
        rows = (reportData.entries || []).map((e) => {
          const isDebit = e.closingBalance > 0;
          return {
            'Customer Code': e.customerCode,
            'Customer Name': e.customerName,
            Debit: isDebit ? e.closingBalance : 0,
            Credit: !isDebit && e.closingBalance !== 0 ? Math.abs(e.closingBalance) : 0,
          };
        });
        break;
      case 'profitLoss':
        rows = [
          { Category: 'Total Revenue (Sales)', Amount: reportData.totalRevenue },
          { Category: 'Discounts Allowed', Amount: -reportData.totalDiscount },
          { Category: 'Cost of Goods Sold (COGS)', Amount: -reportData.totalCogs },
          { Category: 'Gross Profit / (Loss)', Amount: reportData.grossProfit },
        ];
        break;
      default:
        rows = [];
    }

    const worksheet = XLSX.utils.json_to_sheet(rows);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Report');
    const today = new Date().toISOString().split('T')[0];
    XLSX.writeFile(workbook, `${activeTab}_${today}.xlsx`);
  };

  return (
    <div className="page-container report-page">
      <div className="page-header no-print" style={{ marginBottom: '24px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)' }}>Reports & Ledgers</h1>
      </div>

      <div className="report-controls no-print form-card" style={{ background: 'var(--color-card-bg)', borderRadius: 'var(--radius-lg)', padding: '24px', boxShadow: 'var(--shadow-card)', border: '1px solid var(--color-card-border)', marginBottom: '24px' }}>
        <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div className="form-group" style={{ flex: '1', minWidth: '200px' }}>
            <label>Report Type</label>
            <select className="form-control" value={activeTab} onChange={e => { setActiveTab(e.target.value); setReportData(null); }}>
              <option value="salesLedger">Sales Ledger</option>
              <option value="purchaseLedger">Purchase Ledger</option>
              <option value="stockLedger">Stock Ledger</option>
              <option value="salesTaxLedger">Sales Tax Ledger</option>
              <option value="trialBalance">Trial Balance</option>
              <option value="profitLoss">Profit & Loss Statement</option>
            </select>
          </div>

          {activeTab === 'stockLedger' && (
            <div className="form-group" style={{ flex: '1', minWidth: '200px' }}>
              <label>Select Item</label>
              <select className="form-control" value={selectedItemId} onChange={e => setSelectedItemId(e.target.value)}>
                {items.map(i => <option key={i.itemId} value={i.itemId}>{i.itemName}</option>)}
              </select>
            </div>
          )}

          {activeTab !== 'trialBalance' && (
            <div className="form-group" style={{ flex: '1', minWidth: '150px' }}>
              <label>From Date</label>
              <input type="date" className="form-control" value={fromDate} onChange={e => setFromDate(e.target.value)} />
            </div>
          )}

          <div className="form-group" style={{ flex: '1', minWidth: '150px' }}>
            <label>{activeTab === 'trialBalance' ? 'As Of Date' : 'To Date'}</label>
            <input type="date" className="form-control" value={toDate} onChange={e => setToDate(e.target.value)} />
          </div>

          <div style={{ paddingBottom: '4px' }}>
            <button className="btn-primary" onClick={generateReport} disabled={loading}>
              {loading ? 'Generating...' : 'Generate Report'}
            </button>
          </div>
        </div>
      </div>

      {error && <div className="alert alert-error no-print">⚠️ {error}</div>}

      {reportData && (
        <div className="report-output" style={{ background: '#fff', padding: '40px', borderRadius: '8px', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }} className="no-print">
            <h2 style={{ margin: 0 }}>Report Preview</h2>
            <div style={{ display: 'flex', gap: '10px' }}>
              {reportData && (
                <button
                  onClick={exportExcel}
                  style={{ border: '1px solid #10b981', background: '#f0fdf4', color: '#15803d', padding: '6px 16px', borderRadius: '4px', cursor: 'pointer', fontWeight: '600' }}
                >
                  📥 Export Excel
                </button>
              )}
              <button className="btn-secondary" onClick={printReport} style={{ border: '1px solid #94a3b8', background: 'transparent', padding: '6px 16px', borderRadius: '4px', cursor: 'pointer' }}>🖨️ Print</button>
            </div>
          </div>

          <div className="printable-area" style={{ color: '#000' }}>
            <div style={{ textAlign: 'center', marginBottom: '30px', borderBottom: '2px solid #000', paddingBottom: '10px' }}>
              <h1 style={{ margin: '0 0 10px 0', fontSize: '24px' }}>INVENTORY MANAGEMENT SYSTEM</h1>
              <h2 style={{ margin: '0 0 5px 0', fontSize: '18px' }}>
                {activeTab === 'salesLedger' && 'SALES LEDGER'}
                {activeTab === 'purchaseLedger' && 'PURCHASE LEDGER'}
                {activeTab === 'stockLedger' && 'STOCK LEDGER'}
                {activeTab === 'salesTaxLedger' && 'SALES TAX LEDGER'}
                {activeTab === 'trialBalance' && 'TRIAL BALANCE'}
                {activeTab === 'profitLoss' && 'PROFIT & LOSS STATEMENT'}
              </h2>
              <p style={{ margin: 0, fontSize: '14px' }}>
                {activeTab === 'trialBalance' ? `As of: ${toDate}` : `Period: ${fromDate} to ${toDate}`}
              </p>
              {activeTab === 'stockLedger' && (
                <p style={{ margin: '5px 0 0 0', fontWeight: 'bold' }}>Item: {reportData.itemName} ({reportData.itemCode}) - Base Unit: {reportData.unit}</p>
              )}
            </div>

            {/* SALES LEDGER RENDER */}
            {activeTab === 'salesLedger' && (
              <>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', marginBottom: '20px' }}>
                  <thead>
                    <tr style={{ background: '#f1f5f9', borderBottom: '2px solid #000' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Date</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Bill No</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Customer</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Tax (₹)</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Net Amount (₹)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.entries.map((e, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e2e8f0' }}>
                        <td style={{ padding: '8px' }}>{e.billDate}</td>
                        <td style={{ padding: '8px' }}>{e.billNo}</td>
                        <td style={{ padding: '8px' }}>{e.customerName}</td>
                        <td style={{ padding: '8px', textAlign: 'right' }}>{e.taxAmount.toFixed(2)}</td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{e.netAmount.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr style={{ borderTop: '2px solid #000', fontWeight: 'bold' }}>
                      <td colSpan="4" style={{ padding: '10px 8px', textAlign: 'right' }}>Total Sales:</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.grandNetAmount.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </>
            )}

            {/* PURCHASE LEDGER RENDER */}
            {activeTab === 'purchaseLedger' && (
              <>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', marginBottom: '20px' }}>
                  <thead>
                    <tr style={{ background: '#f1f5f9', borderBottom: '2px solid #000' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Date</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Bill No</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Supplier</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Tax (₹)</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Net Amount (₹)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.entries.map((e, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e2e8f0' }}>
                        <td style={{ padding: '8px' }}>{e.purchaseDate}</td>
                        <td style={{ padding: '8px' }}>{e.billNo}</td>
                        <td style={{ padding: '8px' }}>{e.supplierName}</td>
                        <td style={{ padding: '8px', textAlign: 'right' }}>{e.taxAmount.toFixed(2)}</td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{e.netAmount.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr style={{ borderTop: '2px solid #000', fontWeight: 'bold' }}>
                      <td colSpan="4" style={{ padding: '10px 8px', textAlign: 'right' }}>Total Purchases:</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.grandNetAmount.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </>
            )}

            {/* STOCK LEDGER RENDER */}
            {activeTab === 'stockLedger' && (
              <>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', marginBottom: '20px' }}>
                  <thead>
                    <tr style={{ background: '#f1f5f9', borderBottom: '2px solid #000' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Date</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Reference</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>In Qty</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Out Qty</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Balance Qty</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.entries.map((e, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e2e8f0' }}>
                        <td style={{ padding: '8px' }}>{e.date}</td>
                        <td style={{ padding: '8px' }}>{e.reference}</td>
                        <td style={{ padding: '8px', textAlign: 'right', color: 'green' }}>{e.inQty > 0 ? e.inQty : '-'}</td>
                        <td style={{ padding: '8px', textAlign: 'right', color: 'red' }}>{e.outQty > 0 ? e.outQty : '-'}</td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{e.balance}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr style={{ borderTop: '2px solid #000', fontWeight: 'bold' }}>
                      <td colSpan="4" style={{ padding: '10px 8px', textAlign: 'right' }}>Closing Stock:</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>{reportData.closingStock} {reportData.unit}</td>
                    </tr>
                  </tfoot>
                </table>
              </>
            )}

            {/* SALES TAX LEDGER RENDER */}
            {activeTab === 'salesTaxLedger' && (
              <>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', marginBottom: '20px' }}>
                  <thead>
                    <tr style={{ background: '#f1f5f9', borderBottom: '2px solid #000' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Date</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Bill No</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Customer</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Taxable Amt (₹)</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Tax Collected (₹)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.entries.map((e, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e2e8f0' }}>
                        <td style={{ padding: '8px' }}>{e.date}</td>
                        <td style={{ padding: '8px' }}>{e.billNo}</td>
                        <td style={{ padding: '8px' }}>{e.customerName}</td>
                        <td style={{ padding: '8px', textAlign: 'right' }}>{e.taxableAmount.toFixed(2)}</td>
                        <td style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{e.taxAmount.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr style={{ borderTop: '2px solid #000', fontWeight: 'bold' }}>
                      <td colSpan="3" style={{ padding: '10px 8px', textAlign: 'right' }}>Totals:</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.totalTaxableAmount.toFixed(2)}</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.totalTaxCollected.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </>
            )}

            {/* TRIAL BALANCE RENDER */}
            {activeTab === 'trialBalance' && (
              <>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', marginBottom: '20px' }}>
                  <thead>
                    <tr style={{ background: '#f1f5f9', borderBottom: '2px solid #000' }}>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Customer Code</th>
                      <th style={{ padding: '8px', textAlign: 'left' }}>Customer Name</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Debit (Dr) ₹</th>
                      <th style={{ padding: '8px', textAlign: 'right' }}>Credit (Cr) ₹</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.entries.map((e, i) => {
                      const isDebit = e.closingBalance > 0;
                      return (
                        <tr key={i} style={{ borderBottom: '1px solid #e2e8f0' }}>
                          <td style={{ padding: '8px' }}>{e.customerCode}</td>
                          <td style={{ padding: '8px' }}>{e.customerName}</td>
                          <td style={{ padding: '8px', textAlign: 'right' }}>{isDebit ? e.closingBalance.toFixed(2) : '-'}</td>
                          <td style={{ padding: '8px', textAlign: 'right' }}>{!isDebit && e.closingBalance !== 0 ? Math.abs(e.closingBalance).toFixed(2) : '-'}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                  <tfoot>
                    <tr style={{ borderTop: '2px solid #000', fontWeight: 'bold' }}>
                      <td colSpan="2" style={{ padding: '10px 8px', textAlign: 'right' }}>Grand Totals:</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.totalDebit.toFixed(2)}</td>
                      <td style={{ padding: '10px 8px', textAlign: 'right' }}>₹{reportData.totalCredit.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </>
            )}

            {/* PROFIT AND LOSS RENDER */}
            {activeTab === 'profitLoss' && (
              <>
                <div style={{ maxWidth: '600px', margin: '0 auto', border: '1px solid #e2e8f0', borderRadius: '8px', padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px dashed #cbd5e1' }}>
                    <span style={{ fontWeight: '500' }}>Total Revenue (Sales)</span>
                    <span>₹{reportData.totalRevenue.toFixed(2)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px dashed #cbd5e1' }}>
                    <span style={{ fontWeight: '500' }}>Discounts Allowed</span>
                    <span style={{ color: 'red' }}>- ₹{reportData.totalDiscount.toFixed(2)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid #94a3b8' }}>
                    <span style={{ fontWeight: '500' }}>Cost of Goods Sold (COGS)</span>
                    <span style={{ color: 'red' }}>- ₹{reportData.totalCogs.toFixed(2)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '15px 0', fontSize: '18px', fontWeight: 'bold' }}>
                    <span>Gross Profit / (Loss)</span>
                    <span style={{ color: reportData.grossProfit >= 0 ? 'green' : 'red' }}>₹{reportData.grossProfit.toFixed(2)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', background: '#f8fafc', marginTop: '10px', borderRadius: '4px' }}>
                    <span>Gross Profit Margin</span>
                    <span>{reportData.grossProfitPercent.toFixed(2)}%</span>
                  </div>
                </div>
              </>
            )}

          </div>
        </div>
      )}

      <style dangerouslySetInnerHTML={{__html: `
        @media print {
          body * {
            visibility: hidden;
          }
          .printable-area, .printable-area * {
            visibility: visible;
          }
          .printable-area {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
          }
          .no-print {
            display: none !important;
          }
          @page { size: auto;  margin: 10mm; }
        }
      `}} />
    </div>
  );
};

export default ReportsDashboard;
