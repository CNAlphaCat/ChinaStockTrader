import { useState } from 'react';
import TenYearTreasuryBondChart from '../../components/bond/TenYearTreasuryBondChart';
import EquityPremiumIndexChart from '../../components/statistic/EquityPremiumIndexChart';
const MarketValuationPage = () => {

    const getDefaultStartDate = (monthsAgo) => {
        const today = new Date();
        today.setMonth(today.getMonth() - monthsAgo);
        return today.toISOString().split('T')[0];
    };


    const [startDate5Y] = useState(getDefaultStartDate(60));
    const [startDate10Y] = useState(getDefaultStartDate(120));

    return (
        <div  style={{ padding: '20px' }}>
            <EquityPremiumIndexChart startDate={startDate10Y}  showPointsDetail={false} />
            <TenYearTreasuryBondChart startDate={startDate5Y}  showPointsDetail={false} />
        </div>
    );
};

export default MarketValuationPage;