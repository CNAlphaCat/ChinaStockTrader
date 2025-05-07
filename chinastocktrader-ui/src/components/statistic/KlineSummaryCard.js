import React from 'react';

const KlineSummaryCard = ({ analysisData }) => {
    if (!analysisData) return null;
    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
            <h3>{analysisData.stockCode}分析概览</h3>
            <p>股票代码：{analysisData.stockCode}</p>
            <p>起始时间周期：{analysisData.startTime}</p>
            <p>结束时间周期：{analysisData.endTime}</p>
            <p>5分钟K线上涨总数：{analysisData.increaseCount}</p>
            <p>5分钟K线下跌总数：{analysisData.decreaseCount}</p>
            <p>预计切入量：{analysisData.displayRisingAmountInOneMinute}</p>
        </div>
    );
};

export default KlineSummaryCard;