import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './index.css';
import App from './App';


import OnePercentVolatilityFundsPage from './pages/StockChartPage/OnePercentVolatilityFundsPage';
import StockMonitorPage from './pages/StockMonitor/StockMonitorPage';
import StockMonitorConfigPage from './pages/StockMonitor/StockMonitorConfigPage';
import CSI1000DivideCSI300Page from './pages/StockChartPage/CSI1000DivideCSI300Page';
import MarketValuationPage from './pages/dailysummary/MarketValuationPage';
import MarketSummaryPage from './pages/dailysummary/MarketSummaryPage';
import FiveMinutesKlineAnalysis from './pages/StockAnalysis/FiveMinutesKlineAnalysis';
import IMVolatilityReportPage from './pages/report/IMVolatilityReportPage';
import CSIFutureSummaryPage from './pages/dailysummary/CSIFutureSummaryPage';


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
        <Route path="/market-summary" element={<MarketSummaryPage />} />
        <Route path="/market-valuation" element={<MarketValuationPage />} />
        <Route path="/csi-future-summary" element={<CSIFutureSummaryPage />} />
        <Route path="/five-minutes-kline-analysis" element={<FiveMinutesKlineAnalysis />} />
        <Route path="/im-volatility-report" element={<IMVolatilityReportPage />} />
      </Routes>
    </Router>
  </React.StrictMode>
);
