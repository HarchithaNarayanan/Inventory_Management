import React, { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

let toastIdCounter = 0;

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((message, type = 'info') => {
    const id = ++toastIdCounter;
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 3000);
  }, []);

  const removeToast = (id) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  const colorMap = {
    success: { bg: '#ecfdf5', border: '#a7f3d0', color: '#047857', icon: '✓' },
    error:   { bg: '#fef2f2', border: '#fecaca', color: '#b91c1c', icon: '✕' },
    info:    { bg: '#eff6ff', border: '#bfdbfe', color: '#1d4ed8', icon: 'ℹ' },
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}

      {/* Toast Container */}
      <div
        style={{
          position: 'fixed',
          top: '20px',
          right: '20px',
          zIndex: 9999,
          display: 'flex',
          flexDirection: 'column',
          gap: '10px',
          pointerEvents: 'none',
        }}
      >
        <style>{`
          @keyframes toastSlideIn {
            from {
              opacity: 0;
              transform: translateX(110%);
            }
            to {
              opacity: 1;
              transform: translateX(0);
            }
          }
          @keyframes toastFadeOut {
            from { opacity: 1; }
            to   { opacity: 0; }
          }
          .toast-card {
            animation: toastSlideIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
            pointer-events: all;
          }
        `}</style>

        {toasts.map(toast => {
          const style = colorMap[toast.type] || colorMap.info;
          return (
            <div
              key={toast.id}
              className="toast-card"
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                gap: '10px',
                minWidth: '280px',
                maxWidth: '380px',
                background: style.bg,
                border: `1px solid ${style.border}`,
                borderRadius: '8px',
                padding: '12px 16px',
                boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
                color: style.color,
                fontFamily: 'var(--font-body)',
                fontSize: '0.9rem',
                fontWeight: '500',
              }}
            >
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  width: '20px',
                  height: '20px',
                  borderRadius: '50%',
                  background: style.color,
                  color: '#fff',
                  fontSize: '11px',
                  fontWeight: '700',
                  flexShrink: 0,
                  marginTop: '1px',
                }}
              >
                {style.icon}
              </span>
              <span style={{ flex: 1, lineHeight: '1.4' }}>{toast.message}</span>
              <button
                onClick={() => removeToast(toast.id)}
                style={{
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  color: style.color,
                  opacity: 0.6,
                  fontSize: '16px',
                  lineHeight: 1,
                  padding: '0 0 0 4px',
                  flexShrink: 0,
                }}
              >
                ×
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within a ToastProvider');
  return ctx;
};
