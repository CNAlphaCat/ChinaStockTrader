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

const TotalMarketAmountSummaryChart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: TITLE,
                data: [],
                fill: false,
                backgroundColor: 'rgba(75,192,192,0.4)',
                borderColor: 'rgba(75,192,192,1)',
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
                const shenzhenMap = new Map(
                    shenzhenIndexHistory.map(item => [item.tradeDate, item])
                );
    
                const labels = [];
                const totalData = [];
    
                for (const item of shanghaiIndexHistory) {
                    const date = item.tradeDate;
                    const shanghaiAmount = item.amount / 1e8 || 0;
                    const shenzhenItem = shenzhenMap.get(date);
                    const shenzhenAmount = shenzhenItem ? shenzhenItem.amount / 1e8 : 0;
    
                    labels.push(date);
                    totalData.push(shanghaiAmount + shenzhenAmount);
                }

                setChartData({
                    labels: labels,
                    datasets: [
                        {
                            label: TITLE,
                            data: totalData,
                            fill: false,
                            backgroundColor: 'rgba(75,192,192,0.4)',
                            borderColor: 'rgba(75,192,192,1)',
                            spanGaps: true
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

export default TotalMarketAmountSummaryChart;