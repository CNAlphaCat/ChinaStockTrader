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
import { getShanghaiIndexHistory, getShenzhenIndexHistory } from '../../services/IndexHistoryService';

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
                const labels = shanghaiIndexHistory.map((item) => item.tradeDate);

                const lastDate = labels[labels.length - 1];
                setEndDate(lastDate);

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

    const findLastValidValue = (dataArray) => {
        for (let i = dataArray.length - 1; i >= 0; i--) {
            const value = dataArray[i];
            if (value !== null && value !== undefined && !isNaN(value)) {
                return value.toFixed(2);
            }
        }
        return '-';
    };

    const shanghaiData = chartData.datasets[0].data;
    const shenzhenData = chartData.datasets[1].data;

    const shanghai = findLastValidValue(shanghaiData);
    const shenzhen = findLastValidValue(shenzhenData);

    return (
        <div>
            <h2>{TITLE}</h2>
            <div style={{ marginTop: '10px', fontSize: '20px', fontWeight: 'bold' }}>
                最新值 -
                <div style={{ marginLeft: '20px', marginTop: '5px' }}>
                    上海：{shanghai} （{endDate}）
                    <br />
                    深圳：{shenzhen} （{endDate}）
                </div>
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
                                    yValue: (() => {
                                        const allData = [
                                            ...chartData.datasets[0].data.filter(v => v !== null && v !== undefined),
                                            ...chartData.datasets[1].data.filter(v => v !== null && v !== undefined)
                                        ];
                                        return Math.max(...allData) * 0.98;
                                    })(),
                                    backgroundColor: 'rgba(255,255,255,0.7)',
                                    borderWidth: 1,
                                    borderColor: 'gray',
                                    content: [`${startDate} - ${endDate}`],
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

export default MarketAmountSummaryChart;