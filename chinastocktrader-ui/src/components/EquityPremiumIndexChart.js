import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { getEquityPremiumIndex } from '../services/indexStatisticService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const EquityPremiumIndexChart = ({ startDate }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: '股权溢价指数',
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            },
        ],
    });

    useEffect(() => {
        const fetchChartData = async () => {
            if (!startDate) return;
            try {
                const data = await getEquityPremiumIndex(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);
                    const percentile = data.map((item) => item.percentile);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: '股权溢价指数历史百分位',
                                data: percentile,
                                fill: false,
                                backgroundColor: 'rgba(153,102,255,0.4)',
                                borderColor: 'rgba(153,102,255,1)',
                                spanGaps: true
                            },
                        ],
                    });
                } else {
                    console.error('Invalid data format:', data);
                }
            } catch (error) {
                console.error('Error fetching chart data:', error);
            }
        };

        fetchChartData();
    }, [startDate]);

    return (
        <div>
            <h2>股权溢价指数</h2>
            <Line data={chartData} />
        </div>
    );
};

export default EquityPremiumIndexChart;