import React, { useState } from 'react';
import IMVolatilityReportChart from '../../components/report/IMVolatilityReportChart';

const IMVolatilityReportPage = () => {
    const getDefaultStartDate = () => {
        const today = new Date();
        today.setMonth(today.getMonth() - 6);
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
            <IMVolatilityReportChart startDate={startDate}  showPointsDetail={false} />
        </div>
    );
};

export default IMVolatilityReportPage;