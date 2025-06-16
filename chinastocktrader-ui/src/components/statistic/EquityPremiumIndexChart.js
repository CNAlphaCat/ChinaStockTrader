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
import annotationPlugin from 'chartjs-plugin-annotation';

import { getEquityPremiumIndex } from '../../services/IndexStatisticService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);
ChartJS.register(annotationPlugin);

const TITLE = '股权溢价指数';

const EquityPremiumIndexChart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: TITLE,
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            },
        ],
    });

    const fetchTimeoutRef = useRef(null);
    const [endDate, setEndDate] = useState('');
    const [equityPremiumIndex, setEquityPremiumIndex] = useState(0);

    useEffect(() => {
        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }

        if (!startDate) return;

        fetchTimeoutRef.current = setTimeout(async () => {

            try {
                const data = await getEquityPremiumIndex(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);
                    const lastDate = labels[labels.length - 1];
                    setEndDate(lastDate);

                    const percentile = data.map((item) => item.percentile);

                    const equityPremiumIndex = data.find((item) => item.date === lastDate).equityPremiumIndex;
                    setEquityPremiumIndex(equityPremiumIndex);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: '股权溢价指数历史百分位',
                                data: percentile,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
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
        }, 300);

        return () => {
            if (fetchTimeoutRef.current) {
                clearTimeout(fetchTimeoutRef.current);
            }
        };
    }, [startDate]);

    const getChartOptions = (showPointsDetail, chartData, startDate, endDate) => {
        return {
            elements: {
                point: {
                    radius: showPointsDetail ? 3 : 0,
                }
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
                    text: `股权溢价指数历史百分位`,
                    font: {
                        size: 20,
                    },
                },
                annotation: {
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
                                size: 13,
                            },
                            padding: 6,
                        },
                        line80: {
                            type: 'line',
                            yMin: 80,
                            yMax: 80,
                            borderColor: 'red',
                            borderWidth: 2,
                            borderDash: [5, 5],
                            label: {
                                enabled: false,
                                content: '80%',
                            }
                        },
                        line20: {
                            type: 'line',
                            yMin: 20,
                            yMax: 20,
                            borderColor: 'red',
                            borderWidth: 2,
                            borderDash: [5, 5],
                            label: {
                                enabled: false,
                                content: '20%',
                            }
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
        };
    };

    return (
        <div>
            <h2>股权溢价指数</h2>
            <p style={{ marginTop: '10px', fontStyle: 'italic' }}>
                历史上有多少时间比现在的股权溢价指数低
            </p>
            <div style={{ marginTop: '10px', fontSize: '20px', fontWeight: 'bold' }}>
                百分位最新值：{chartData.datasets[0].data[chartData.datasets[0].data.length - 1]?.toFixed(2) || '-'} （{endDate}）
                <br />
                股权溢价指数：{equityPremiumIndex}（{endDate}）
            </div>
            <Line
                data={chartData}
                options={getChartOptions(showPointsDetail, chartData, startDate, endDate)}
            />
        </div>
    );
};

export default EquityPremiumIndexChart;