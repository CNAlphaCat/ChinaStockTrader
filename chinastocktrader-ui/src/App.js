import React from 'react';
import { Link } from 'react-router-dom';

function App() {
  return (
    <div>
      <h1>导航页</h1>
      <ul>
        <li><Link to="/one-percent-volatility">1% 波动率基金</Link></li>
        <li><Link to="/stock-config">股票监控配置</Link></li>
        <li><Link to="/csi1000-divide-csi300">CSI1000 / CSI300</Link></li>
        <li><Link to="/market-summary">每日市场情况总结</Link></li>
        <li><Link to="/market-valuation">市场估值</Link></li>
        <li><Link to="/csi-future-summary">股指期货总结</Link></li>
        <li><Link to="/five-minutes-kline-analysis">五分钟K线分析</Link></li>
      </ul>
    </div>
  );
}

export default App;
