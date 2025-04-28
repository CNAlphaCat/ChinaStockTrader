import React, { useState } from 'react';
import CSI1000DivideCSI300Chart from '../../components/CSI1000DivideCSI300Chart';

const CSI1000DivideCSI300Page = () => {
    const getDefaultStartDate = () => {
        const today = new Date();
        today.setMonth(today.getMonth() - 3);
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
            <CSI1000DivideCSI300Chart startDate={startDate} />
            <div style={{ marginTop: '20px' }}>
                <h3>参考图表</h3>
                <img 
                    src="/pic/CSI1000Divide300policy.jpg" 
                    alt="1000/300比价图表" 
                    style={{ maxWidth: '100%', height: 'auto' }} 
                />
            </div>
        </div>
    );
};

export default CSI1000DivideCSI300Page;