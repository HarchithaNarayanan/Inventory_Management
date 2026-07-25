import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';

import DashboardLayout from './layouts/DashboardLayout';
import SupplierList from './pages/suppliers/SupplierList';
import SupplierForm from './pages/suppliers/SupplierForm';
import CustomerList from './pages/customers/CustomerList';
import CustomerForm from './pages/customers/CustomerForm';
import ItemList from './pages/items/ItemList';
import ItemForm from './pages/items/ItemForm';
import BankList from './pages/banks/BankList';
import BankForm from './pages/banks/BankForm';
import CompanyList from './pages/companies/CompanyList';
import CompanyForm from './pages/companies/CompanyForm';
import PurchaseList from './pages/purchases/PurchaseList';
import PurchaseForm from './pages/purchases/PurchaseForm';
import PurchaseDetail from './pages/purchases/PurchaseDetail';
import BillingList from './pages/billing/BillingList';
import BillingForm from './pages/billing/BillingForm';
import BillingDetail from './pages/billing/BillingDetail';
import AccountTransactionList from './pages/transactions/AccountTransactionList';
import AccountTransactionForm from './pages/transactions/AccountTransactionForm';
import ReportsDashboard from './pages/reports/ReportsDashboard';
import UserList from './pages/users/UserList';

import Dashboard from './pages/Dashboard';

// Unauthorized / Access Denied view
const UnauthorizedView = () => (
  <div style={{
    minHeight: '100vh',
    backgroundColor: 'var(--color-bg-main)',
    color: 'var(--color-text-primary)',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center'
  }}>
    <h1 style={{ color: 'var(--color-error)', marginBottom: '16px' }}>403 - Access Denied</h1>
    <p style={{ color: 'var(--color-text-secondary)', marginBottom: '24px' }}>You do not have the required permissions to view this resource.</p>
    <Link to="/dashboard" className="auth-link">Return to Dashboard</Link>
  </div>
);

const App = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/unauthorized" element={<UnauthorizedView />} />

          {/* Protected Routes Wrapper with Sidebar Layout */}
          <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF']} />}>
            <Route element={<DashboardLayout />}>
              <Route path="/dashboard" element={<Dashboard />} />
              
              {/* Supplier Routes (Only Admin and Manager) */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/suppliers" element={<SupplierList />} />
                <Route path="/suppliers/new" element={<SupplierForm />} />
                <Route path="/suppliers/edit/:id" element={<SupplierForm />} />
              </Route>

              {/* Customer Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/customers" element={<CustomerList />} />
                <Route path="/customers/new" element={<CustomerForm />} />
                <Route path="/customers/edit/:id" element={<CustomerForm />} />
              </Route>

              {/* Item Routes (Staff can access) */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF']} />}>
                <Route path="/items" element={<ItemList />} />
                <Route path="/items/new" element={<ItemForm />} />
                <Route path="/items/edit/:id" element={<ItemForm />} />
              </Route>

              {/* Bank Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/banks" element={<BankList />} />
                <Route path="/banks/new" element={<BankForm />} />
                <Route path="/banks/edit/:id" element={<BankForm />} />
              </Route>

              {/* Company Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/companies" element={<CompanyList />} />
                <Route path="/companies/new" element={<CompanyForm />} />
                <Route path="/companies/edit/:id" element={<CompanyForm />} />
              </Route>

              {/* Purchase (Receipt) Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/purchases" element={<PurchaseList />} />
                <Route path="/purchases/new" element={<PurchaseForm />} />
                <Route path="/purchases/:id" element={<PurchaseDetail />} />
              </Route>

              {/* Billing (Sales) Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF']} />}>
                <Route path="/billing" element={<BillingList />} />
                <Route path="/billing/new" element={<BillingForm />} />
                <Route path="/billing/:id" element={<BillingDetail />} />
              </Route>

              {/* Account Transaction Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF']} />}>
                <Route path="/transactions" element={<AccountTransactionList />} />
                <Route path="/transactions/new" element={<AccountTransactionForm />} />
              </Route>

              {/* Reports Dashboard */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']} />}>
                <Route path="/reports" element={<ReportsDashboard />} />
              </Route>

              {/* User Management (Admin only) */}
              <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
                <Route path="/users" element={<UserList />} />
              </Route>

            </Route>
          </Route>

          {/* Root Redirect */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          
          {/* Fallback Route */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
