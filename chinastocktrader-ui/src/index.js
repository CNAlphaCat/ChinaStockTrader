import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './index.css';
import App from './App';
import OnePercentVolatilityFundsPage from './pages/StockChartPage/OnePercentVolatilityFundsPage';
import StockMonitorPage from './pages/StockMonitor/StockMonitorPage';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
        <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/one-percent-volatility" element={<OnePercentVolatilityFundsPage />} />
        <Route path="/stock-monitor" element={<StockMonitorPage />} />
      </Routes>
    </Router>
  </React.StrictMode>
);
