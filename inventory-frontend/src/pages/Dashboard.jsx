import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const isAdminOrManager = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_MANAGER';

  
  const [stats, setStats] = useState({
    customers: 0,
    suppliers: 0,
    items: 0,
    sales: 0,
    purchases: 0
  });

  const [recentSales, setRecentSales] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      // Fetch all required data using allSettled so one failure (like 403 for Staff) doesn't break the whole dashboard
      const results = await Promise.allSettled([
        api.get('/api/customers'),
        api.get('/api/suppliers'),
        api.get('/api/items'),
        api.get('/api/billings'),
        api.get('/api/purchases')
      ]);

      const getResultData = (result) => result.status === 'fulfilled' ? (result.value.data.data || []) : [];

      const customers = getResultData(results[0]);
      const suppliers = getResultData(results[1]);
      const items = getResultData(results[2]);
      const sales = getResultData(results[3]);
      const purchases = getResultData(results[4]);

      // Calculate totals
      const totalSalesAmt = sales.reduce((sum, bill) => sum + bill.netAmount, 0);
      const totalPurchasesAmt = purchases.reduce((sum, pur) => sum + pur.netAmount, 0);

      setStats({
        customers: customers.length,
        suppliers: suppliers.length,
        items: items.length,
        sales: totalSalesAmt,
        purchases: totalPurchasesAmt
      });

      // Get 5 most recent sales for the quick table
      const sortedSales = [...sales].sort((a, b) => new Date(b.billDate) - new Date(a.billDate));
      setRecentSales(sortedSales.slice(0, 5));

    } catch (err) {
      console.error("Failed to load dashboard data", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div style={{ padding: '40px', textAlign: 'center', color: 'var(--color-text-secondary)' }}>Loading Dashboard...</div>;
  }

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontFamily: 'var(--font-heading)', color: 'var(--color-text-primary)', marginBottom: '8px' }}>
            Dashboard Overview
          </h1>
          <p style={{ color: 'var(--color-text-secondary)', margin: 0 }}>
            Welcome back, {user?.employeeName || 'Admin'}! Here's what's happening today.
          </p>
        </div>
      </div>

      {/* KPI Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '24px', marginBottom: '32px' }}>
        
        <div className="kpi-card" style={{ background: 'linear-gradient(135deg, #0ea5e9, #2563eb)', color: 'white', padding: '24px', borderRadius: '16px', boxShadow: '0 10px 15px -3px rgba(37, 99, 235, 0.3)' }}>
          <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px', fontWeight: '500', textTransform: 'uppercase', letterSpacing: '1px' }}>Total Sales</div>
          <div style={{ fontSize: '32px', fontWeight: 'bold' }}>₹{stats.sales.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</div>
        </div>

        {isAdminOrManager && (
          <div className="kpi-card" style={{ background: 'linear-gradient(135deg, #10b981, #059669)', color: 'white', padding: '24px', borderRadius: '16px', boxShadow: '0 10px 15px -3px rgba(16, 185, 129, 0.3)' }}>
            <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px', fontWeight: '500', textTransform: 'uppercase', letterSpacing: '1px' }}>Total Purchases</div>
            <div style={{ fontSize: '32px', fontWeight: 'bold' }}>₹{stats.purchases.toLocaleString('en-IN', { maximumFractionDigits: 0 })}</div>
          </div>
        )}

        <div className="kpi-card" style={{ background: 'var(--color-card-bg)', border: '1px solid var(--color-card-border)', padding: '24px', borderRadius: '16px', boxShadow: 'var(--shadow-card)' }}>
          <div style={{ fontSize: '14px', color: 'var(--color-text-secondary)', marginBottom: '8px', fontWeight: '500', textTransform: 'uppercase', letterSpacing: '1px' }}>Total Items in Stock</div>
          <div style={{ fontSize: '32px', fontWeight: 'bold', color: 'var(--color-text-primary)' }}>{stats.items}</div>
        </div>

        {isAdminOrManager && (
          <div className="kpi-card" style={{ background: 'var(--color-card-bg)', border: '1px solid var(--color-card-border)', padding: '24px', borderRadius: '16px', boxShadow: 'var(--shadow-card)' }}>
            <div style={{ fontSize: '14px', color: 'var(--color-text-secondary)', marginBottom: '8px', fontWeight: '500', textTransform: 'uppercase', letterSpacing: '1px' }}>Total Customers</div>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: 'var(--color-text-primary)' }}>{stats.customers}</div>
          </div>
        )}

      </div>

      {/* Financial Overview Chart */}
      {isAdminOrManager && (
        <div style={{ background: 'var(--color-card-bg)', borderRadius: '16px', border: '1px solid var(--color-card-border)', padding: '24px', boxShadow: 'var(--shadow-card)', marginBottom: '32px' }}>
          <h2 style={{ margin: '0 0 20px 0', fontSize: '18px', color: 'var(--color-text-primary)' }}>Financial Overview</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={[
                {
                  name: 'Financial Summary',
                  Sales: stats.sales,
                  Purchases: stats.purchases
                }
              ]}
              margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="name" tick={{ fill: 'var(--color-text-secondary)', fontSize: 13 }} />
              <YAxis
                tick={{ fill: 'var(--color-text-secondary)', fontSize: 12 }}
                tickFormatter={(value) => `₹${value.toLocaleString('en-IN', { maximumFractionDigits: 0 })}`}
              />
              <Tooltip
                formatter={(value, name) => [
                  `₹${value.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
                  name
                ]}
                contentStyle={{
                  background: 'var(--color-card-bg)',
                  border: '1px solid var(--color-card-border)',
                  borderRadius: '8px',
                  color: 'var(--color-text-primary)'
                }}
              />
              <Legend wrapperStyle={{ paddingTop: '16px', color: 'var(--color-text-secondary)' }} />
              <Bar dataKey="Sales" fill="#0ea5e9" radius={[6, 6, 0, 0]} name="Total Sales" />
              <Bar dataKey="Purchases" fill="#10b981" radius={[6, 6, 0, 0]} name="Total Purchases" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Main Content Area */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        
        {/* Recent Sales Table */}
        <div style={{ background: 'var(--color-card-bg)', borderRadius: '16px', border: '1px solid var(--color-card-border)', padding: '24px', boxShadow: 'var(--shadow-card)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h2 style={{ margin: 0, fontSize: '18px', color: 'var(--color-text-primary)' }}>Recent Sales</h2>
            <button onClick={() => navigate('/billing')} style={{ background: 'transparent', border: 'none', color: '#0ea5e9', cursor: 'pointer', fontWeight: '600' }}>View All →</button>
          </div>
          
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid #f1f5f9', textAlign: 'left', color: 'var(--color-text-secondary)' }}>
                <th style={{ padding: '12px 8px' }}>Bill No</th>
                <th style={{ padding: '12px 8px' }}>Customer</th>
                <th style={{ padding: '12px 8px' }}>Date</th>
                <th style={{ padding: '12px 8px', textAlign: 'right' }}>Amount</th>
              </tr>
            </thead>
            <tbody>
              {recentSales.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '24px', color: 'var(--color-text-secondary)' }}>No recent sales found.</td>
                </tr>
              ) : (
                recentSales.map(sale => (
                  <tr key={sale.billingId} style={{ borderBottom: '1px solid #f1f5f9' }}>
                    <td style={{ padding: '12px 8px', fontWeight: '500', color: 'var(--color-text-primary)' }}>{sale.billNo}</td>
                    <td style={{ padding: '12px 8px', color: 'var(--color-text-primary)' }}>{sale.customerName}</td>
                    <td style={{ padding: '12px 8px', color: 'var(--color-text-secondary)' }}>{sale.billDate}</td>
                    <td style={{ padding: '12px 8px', textAlign: 'right', fontWeight: 'bold', color: 'var(--color-text-primary)' }}>
                      ₹{sale.netAmount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Quick Actions */}
        <div style={{ background: 'var(--color-card-bg)', borderRadius: '16px', border: '1px solid var(--color-card-border)', padding: '24px', boxShadow: 'var(--shadow-card)' }}>
          <h2 style={{ margin: '0 0 20px 0', fontSize: '18px', color: 'var(--color-text-primary)' }}>Quick Actions</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <button onClick={() => navigate('/billing/new')} style={{ padding: '16px', background: '#f0f9ff', border: '1px solid #bae6fd', borderRadius: '8px', color: '#0369a1', fontWeight: '600', cursor: 'pointer', textAlign: 'left', display: 'flex', alignItems: 'center', gap: '12px', transition: 'all 0.2s' }}>
              <span style={{ fontSize: '20px' }}>📦</span> Create New Sale
            </button>
            {isAdminOrManager && (
              <button onClick={() => navigate('/purchases/new')} style={{ padding: '16px', background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', color: '#15803d', fontWeight: '600', cursor: 'pointer', textAlign: 'left', display: 'flex', alignItems: 'center', gap: '12px', transition: 'all 0.2s' }}>
                <span style={{ fontSize: '20px' }}>📥</span> Receive Material
              </button>
            )}
            <button onClick={() => navigate('/transactions/new')} style={{ padding: '16px', background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px', color: '#b91c1c', fontWeight: '600', cursor: 'pointer', textAlign: 'left', display: 'flex', alignItems: 'center', gap: '12px', transition: 'all 0.2s' }}>
              <span style={{ fontSize: '20px' }}>💸</span> Record Payment
            </button>
            {isAdminOrManager && (
              <button onClick={() => navigate('/reports')} style={{ padding: '16px', background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: '8px', color: '#334155', fontWeight: '600', cursor: 'pointer', textAlign: 'left', display: 'flex', alignItems: 'center', gap: '12px', transition: 'all 0.2s' }}>
                <span style={{ fontSize: '20px' }}>📊</span> View Reports
              </button>
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
