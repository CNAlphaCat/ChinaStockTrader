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
    const [endDate, setEndDate] = useState('');

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
                const lastDate = labels[labels.length - 1];
                setEndDate(lastDate);

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
            <div style={{ marginTop: '10px', fontSize: '20px', fontWeight: 'bold' }}>
                最新值：{chartData.datasets[0].data[chartData.datasets[0].data.length - 1]?.toFixed(2) || '-'}
            </div>
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
                        }, annotation: {
                            annotations: {
                                timeRangeLabel: {
                                    type: 'label',
                                    xValue: chartData.labels[Math.floor(chartData.labels.length * 0.07)],
                                    yValue: Math.max(...chartData.datasets[0].data) * 0.98,
                                    backgroundColor: 'rgba(255,255,255,0.7)',
                                    borderWidth: 1,
                                    borderColor: 'gray',
                                    content: [` ${startDate} - ${endDate}`],
                                    font: {
                                        size: 16,
                                    },
                                    padding: 6,
                                }
                            }
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