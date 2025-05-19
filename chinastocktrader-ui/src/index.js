import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './index.css';
import App from './App';


import OnePercentVolatilityFundsPage from './pages/StockChartPage/OnePercentVolatilityFundsPage';
import StockMonitorPage from './pages/StockMonitor/StockMonitorPage';
import StockMonitorConfigPage from './pages/StockMonitor/StockMonitorConfigPage';
import CSI1000DivideCSI300Page from './pages/StockChartPage/CSI1000DivideCSI300Page';
import DailySummaryPage from './pages/StockChartPage/DailySummaryPage';
import FiveMinutesKlineAnalysis from './pages/StockAnalysis/FiveMinutesKlineAnalysis';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/one-percent-volatility" element={<OnePercentVolatilityFundsPage />} />
        <Route path="/stock-config" element={<StockMonitorConfigPage />} />
        <Route path="/stock-monitor" element={<StockMonitorPage />} />
        <Route path="/csi1000-divide-csi300" element={<CSI1000DivideCSI300Page />} />
        <Route path="/daily-summary" element={<DailySummaryPage />} />
        <Route path="/five-minutes-kline-analysis" element={<FiveMinutesKlineAnalysis />} />
      </Routes>
    </Router>
  </React.StrictMode>
);
