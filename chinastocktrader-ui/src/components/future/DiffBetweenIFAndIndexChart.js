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
import { getDiffBetweenIFAndIndex } from '../../services/future/FutureService';

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

const TITLE = 'IF主力合约与现货差值';

const DiffBetweenIMAndIndex = ({ startYear, startMonth, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: 'IF主力合约与现货差值',
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            }, {
                label: 'IF主力合约与现货最近月差值',
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            }
        ],
    });

    const fetchTimeoutRef = useRef(null);
    const [endDate, setEndDate] = useState('');
    const [startDate, setStartDate] = useState('');

    useEffect(() => {
        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }

        if (startYear == null || startMonth == null) return;


        fetchTimeoutRef.current = setTimeout(async () => {

            try {
                const data = await getDiffBetweenIFAndIndex(startYear, startMonth);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);
                    const endDate = labels[labels.length - 1];
                    setEndDate(endDate);
                    setStartDate(labels[0]);

                    const mainDiff = data.map((item) => item.mainDiff);
                    const recentlyMonthDiff = data.map((item) => item.recentlyMonthDiff);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: 'IF主力合约与现货差值',
                                data: mainDiff,
                                fill: false,
                                backgroundColor: 'rgba(153,102,255,0.4)',
                                borderColor: 'rgba(153,102,255,1)',
                                spanGaps: true
                            },
                            {
                                label: 'IF主力合约与现货最近月差值',
                                data: recentlyMonthDiff,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
                                spanGaps: true
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
    }, [startYear, startMonth]);

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
                    text: TITLE,
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
            <h2>{TITLE}</h2>
            <p style={{ marginTop: '10px', fontStyle: 'italic' }}>
                当日成交量最大的IF期货与现货的差值
            </p>
            <div style={{ marginTop: '10px', fontSize: '20px', fontWeight: 'bold' }}>
                主力差值最新值：{chartData.datasets[0].data[chartData.datasets[0].data.length - 1]?.toFixed(2) || '-'} （{endDate}）
                <br />
                最近月差值最新值：{chartData.datasets[1].data[chartData.datasets[1].data.length - 1]?.toFixed(2) || '-'} （{endDate}）
            </div>
            <Line
                data={chartData}
                options={getChartOptions(showPointsDetail, chartData, startDate, endDate)}
            />
        </div>
    );

};

export default DiffBetweenIMAndIndex;