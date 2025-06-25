import React, { useState } from 'react';
import TenYearTreasuryBondChart from '../../components/bond/TenYearTreasuryBondChart';
import EquityPremiumIndexChart from '../../components/statistic/EquityPremiumIndexChart';
import CSI1000DivideCSI300Chart from '../../components/marketindex/CSI1000DivideCSI300Chart';
import OnePercentVolatilityFundsChart from '../../components/marketindex/OnePercentVolatilityFundsChart';
import StockLimitChart from '../../components/market/StockLimitChart';
import StockLimitLogOddsChart from '../../components/market/StockLimitLogOddsChart';
import MarketAmountSummaryChart from '../../components/marketindex/MarketAmountSummaryChart';
import TotalMarketAmountSummaryChart from '../../components/marketindex/TotalMarketAmountSummaryChart';
import DiffBetweenIMAndIndexChart from '../../components/future/DiffBetweenIMAndIndexChart';
import DiffBetweenIFAndIndexChart from '../../components/future/DiffBetweenIFAndIndexChart';
const DailySummaryPage = () => {

    const getDefaultStartDate = (monthsAgo) => {
        const today = new Date();
        today.setMonth(today.getMonth() - monthsAgo);
        return today.toISOString().split('T')[0];
    };

    const getDefaultStartYear = (yearsAgo) => {
        const today = new Date();
        return today.getFullYear() - yearsAgo;
    };

    const [startDate1Y] = useState(getDefaultStartDate(12));
    const [startDate5Y] = useState(getDefaultStartDate(60));
    const [startDate10Y] = useState(getDefaultStartDate(120));
    const [startDate6M] = useState(getDefaultStartDate(6));
    const [startYear1Y] = useState(getDefaultStartYear(1));
    const [startYear5Y] = useState(getDefaultStartYear(5));

    return (
        <div  style={{ padding: '20px' }}>
            <EquityPremiumIndexChart startDate={startDate10Y}  showPointsDetail={false} />
            <TenYearTreasuryBondChart startDate={startDate5Y}  showPointsDetail={false} />
            <TotalMarketAmountSummaryChart startDate={startDate1Y}  showPointsDetail={false} />
            <MarketAmountSummaryChart startDate={startDate1Y}  showPointsDetail={false} />
            <StockLimitLogOddsChart showPointsDetail={false} />
            <StockLimitChart showPointsDetail={false} />
            <CSI1000DivideCSI300Chart startDate={startDate1Y} showPointsDetail={false} />
            <DiffBetweenIMAndIndexChart startYear={startYear1Y} startMonth={0} showPointsDetail={false} />
            <DiffBetweenIFAndIndexChart startYear={startYear1Y} startMonth={0} showPointsDetail={false} />
            <OnePercentVolatilityFundsChart startDate={startDate6M}  showPointsDetail={false} />
        </div>
    );
};

export default DailySummaryPage;