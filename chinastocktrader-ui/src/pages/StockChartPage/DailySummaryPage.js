import React, { useState } from 'react';
import TenYearTreasuryBondChart from '../../components/TenYearTreasuryBondChart';
import EquityPremiumIndexChart from '../../components/EquityPremiumIndexChart';

const DailySummaryPage = () => {
    const getDefaultStartDate = () => {
        const today = new Date();
        today.setMonth(today.getMonth() - 12);
        return today.toISOString().split('T')[0];
    };

    const [startDate, setStartDate] = useState(getDefaultStartDate());

    const handleDateChange = (event) => {
        setStartDate(event.target.value);
    };

    return (
        <div>
            <div>
                <label htmlFor="start-date">选择起始日期: </label>
                <input
                    type="date"
                    id="start-date"
                    value={startDate}
                    onChange={handleDateChange}
                />
            </div>
            <TenYearTreasuryBondChart startDate={startDate} />
            <EquityPremiumIndexChart startDate={startDate} />
        </div>
    );
};

export default DailySummaryPage;