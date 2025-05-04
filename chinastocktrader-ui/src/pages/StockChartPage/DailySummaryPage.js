import React, { useState } from 'react';
import TenYearTreasuryBondChart from '../../components/TenYearTreasuryBondChart';
import EquityPremiumIndexChart from '../../components/EquityPremiumIndexChart';
import CSI1000DivideCSI300Chart from '../../components/CSI1000DivideCSI300Chart';
import OnePercentVolatilityFundsChart from '../../components/OnePercentVolatilityFundsChart';
import StockLimitChart from '../../components/StockLimitChart';
const DailySummaryPage = () => {

    const getDefaultStartDate = (monthsAgo) => {
        const today = new Date();
        today.setMonth(today.getMonth() - monthsAgo);
        return today.toISOString().split('T')[0];
    };

    const [startDate1Y] = useState(getDefaultStartDate(12));
    const [startDate5Y] = useState(getDefaultStartDate(60));
    const [startDate10Y] = useState(getDefaultStartDate(120));

    return (
        <div>
            <EquityPremiumIndexChart startDate={startDate10Y}  showPointsDetail={false} />
            <StockLimitChart showPointsDetail={false} />
            <CSI1000DivideCSI300Chart startDate={startDate1Y} showPointsDetail={false} />
            <OnePercentVolatilityFundsChart startDate={startDate1Y}  showPointsDetail={false} />  
            <TenYearTreasuryBondChart startDate={startDate5Y}  showPointsDetail={false} />
        </div>
    );
};

export default DailySummaryPage;