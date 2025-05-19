import React, { useState } from 'react';
import KlineSummaryCard from '../../components/statistic/KlineSummaryCard';
import { getFiveMinutesKlineAnalysis } from '../../services/StockAnalysisService';

const FiveMinutesKlineAnalysis = () => {
  const [inputCodes, setInputCodes] = useState('');
  const [stockCodes, setStockCodes] = useState([]);
  const [analysisData, setAnalysisData] = useState(null);


  const handleFetchData = async () => {
    try {
      const codes = inputCodes.split(',').map(code => code.trim());
      setStockCodes(codes);

      const promises = stockCodes.map(code => getFiveMinutesKlineAnalysis(code));
      const results = await Promise.all(promises);
      setAnalysisData(results);
    } catch (error) {
      console.error('Error fetching five minutes kline analysis:', error);
    }
  }

  return (
    <div>
      <h2>五分钟K线分析</h2>
      <div>
        <input
          value={inputCodes}
          onChange={(e) => setInputCodes(e.target.value)}
          placeholder="输入股票代码，逗号分隔"
        />
        <button onClick={handleFetchData}>加载数据</button>
      </div>
      {analysisData && analysisData.map((data, index) => (
        <KlineSummaryCard key={index} analysisData={data} />
      ))}
    </div>
  );
}

export default FiveMinutesKlineAnalysis;