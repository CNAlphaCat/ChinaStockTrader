import React, { useState, useEffect, useRef } from 'react';
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
import { getStockLimitSummary } from '../services/marketStatisticService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const StockLimitChart = ({ showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: '涨跌停家数',
                data: [],
                fill: false,
                backgroundColor: 'rgba(75,192,192,0.4)',
                borderColor: 'rgba(75,192,192,1)',
            },
        ],
    });

    const fetchTimeoutRef = useRef(null);

    useEffect(() => {
        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }
        fetchTimeoutRef.current = setTimeout(async () => {
            try {
                const data = await getStockLimitSummary();

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.tradeDate);
                    const limitUpCount = data.map((item) => item.limitUpCount);
                    const limitDownCount = data.map((item) => item.limitDownCount);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: '涨停家数',
                                data: limitUpCount,
                                fill: false,
                                backgroundColor: 'rgba(153,102,255,0.4)',
                                borderColor: 'rgba(153,102,255,1)',
                            },
                            {
                                label: '跌停家数',
                                data: limitDownCount,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
                            }
                        ],
                    });
                } else {
                    console.error('Invalid data format:', data);
                }
            } catch (error) {
                console.error('Error fetching chart data:', error);
            }
        }, 300);

        return () => {
            if (fetchTimeoutRef.current) {
                clearTimeout(fetchTimeoutRef.current);
            }
        };
    }, []);

    return (
        <div>
            <h2>沪深京涨跌停家数</h2>
            <Line
                data={chartData}
                options={{
                    elements: {
                        point: {
                            radius: showPointsDetail ? 3 : 0,
                        },
                    },
                    plugins: {
                        legend: {
                            labels: {
                                font: {
                                    size: 20,
                                },
                            },
                        },
                        title: {
                            display: true,
                            text: '沪深京涨跌停家数',
                            font: {
                                size: 20,
                            },
                        }
                    },
                    scales: {
                        x: {
                            ticks: {
                                font: {
                                    size: 14,
                                },
                            },
                        },
                        y: {
                            ticks: {
                                font: {
                                    size: 14,
                                },
                            },
                        },
                    },
                }}
            />
        </div>
    );
};

export default StockLimitChart;
