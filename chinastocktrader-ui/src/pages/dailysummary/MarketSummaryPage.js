import { useState } from 'react';
import OnePercentVolatilityFundsChart from '../../components/marketindex/OnePercentVolatilityFundsChart';
import StockLimitChart from '../../components/market/StockLimitChart';
import StockLimitLogOddsChart from '../../components/market/StockLimitLogOddsChart';
import MarketAmountSummaryChart from '../../components/marketindex/MarketAmountSummaryChart';
import TotalMarketAmountSummaryChart from '../../components/marketindex/TotalMarketAmountSummaryChart';
const MarketSummaryPage = () => {

    const getDefaultStartDate = (monthsAgo) => {
        const today = new Date();
        today.setMonth(today.getMonth() - monthsAgo);
        return today.toISOString().split('T')[0];
    };

    const [startDate1Y] = useState(getDefaultStartDate(12));
    const [startDate6M] = useState(getDefaultStartDate(6));

    return (
        <div  style={{ padding: '20px' }}>
            <TotalMarketAmountSummaryChart startDate={startDate1Y}  showPointsDetail={false} />
            <MarketAmountSummaryChart startDate={startDate1Y}  showPointsDetail={false} />
            <StockLimitLogOddsChart showPointsDetail={false} />
            <StockLimitChart showPointsDetail={false} />
            <OnePercentVolatilityFundsChart startDate={startDate6M}  showPointsDetail={false} />
        </div>
    );
};

export default MarketSummaryPage;