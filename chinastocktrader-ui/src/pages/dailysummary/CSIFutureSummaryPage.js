import { useState } from 'react';
import CSI1000DivideCSI300Chart from '../../components/marketindex/CSI1000DivideCSI300Chart';
import DiffBetweenIMAndIndexChart from '../../components/future/DiffBetweenIMAndIndexChart';
import DiffBetweenIFAndIndexChart from '../../components/future/DiffBetweenIFAndIndexChart';
const CSIFutureSummaryPage = () => {

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
    const [startYear1Y] = useState(getDefaultStartYear(1));

    return (
        <div  style={{ padding: '20px' }}>
            <CSI1000DivideCSI300Chart startDate={startDate1Y} showPointsDetail={false} />
            <DiffBetweenIMAndIndexChart startYear={startYear1Y} startMonth={0} showPointsDetail={false} />
            <DiffBetweenIFAndIndexChart startYear={startYear1Y} startMonth={0} showPointsDetail={false} />
        </div>
    );
};

export default CSIFutureSummaryPage;