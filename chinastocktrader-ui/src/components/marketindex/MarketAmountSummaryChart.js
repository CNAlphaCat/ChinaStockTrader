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
import { getShanghaiIndexHistory, getShenzhenIndexHistory } from '../../services/indexHistoryService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const TITLE = '沪深两市成交量（亿）';

const MarketAmountSummaryChart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: '上海市场成交量',
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            }, {
                label: '深圳市场成交量',
                data: [],
                fill: false,
                backgroundColor: 'rgba(75,192,192,0.4)',
                borderColor: 'rgba(75,192,192,1)',
                spanGaps: true,
            }
        ],
    });

    const fetchTimeoutRef = useRef(null);

    useEffect(() => {
        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }

        fetchTimeoutRef.current = setTimeout(async () => {
            if (!startDate) return;
            try {
                const shanghaiIndexHistory = await getShanghaiIndexHistory(startDate);
                const shenzhenIndexHistory = await getShenzhenIndexHistory(startDate);

                if (!Array.isArray(shanghaiIndexHistory) || !Array.isArray(shenzhenIndexHistory)) {
                    console.error('Invalid data format:', shanghaiIndexHistory, shenzhenIndexHistory);
                    return;
                }
                const labels = shanghaiIndexHistory.map((item) => item.tradeDate);
                const shanghaiAmount = shanghaiIndexHistory.map((item) => item.amount / 100000000);
                const shenzhenAmount = shenzhenIndexHistory.map((item) => item.amount / 100000000);

                setChartData({
                    labels: labels,
                    datasets: [
                        {
                            label: '上海市场成交量',
                            data: shanghaiAmount,
                            fill: false,
                            backgroundColor: 'rgba(153,102,255,0.4)',
                            borderColor: 'rgba(153,102,255,1)',
                            spanGaps: true
                        }, {
                            label: '深圳市场成交量',
                            data: shenzhenAmount,
                            fill: false,
                            backgroundColor: 'rgba(75,192,192,0.4)',
                            borderColor: 'rgba(75,192,192,1)',
                            spanGaps: true,
                        }
                    ],
                });

            } catch (error) {
                console.error('Error fetching chart data:', error);
            }
        }, 300);
        return () => {
            if (fetchTimeoutRef.current) {
                clearTimeout(fetchTimeoutRef.current);
            }
        };
    }, [startDate]);

    return (
        <div>
            <h2>{TITLE}</h2>
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
                            text: TITLE,
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

export default MarketAmountSummaryChart;