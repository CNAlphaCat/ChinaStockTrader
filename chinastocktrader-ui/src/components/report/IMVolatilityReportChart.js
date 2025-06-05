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
import { getIMVolatilityReport } from '../../services/report/IMVolatilityReportService';

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

const TITLE = 'IM主连波动幅度回测报告';

const IMVolatilityReportChart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        highLow: {
            labels: [], datasets: [
                {
                    label: 'IM主连波动幅度回测报告',
                    data: [],
                    fill: false,
                    backgroundColor: 'rgba(153,102,255,0.4)',
                    borderColor: 'rgba(153,102,255,1)',
                },
            ],
        },
        percentile: {
            labels: [], datasets: [
                {
                    label: 'IM主连波动幅度百分比回测报告',
                    data: [],
                    fill: false,
                    backgroundColor: 'rgba(153,102,255,0.4)',
                    borderColor: 'rgba(153,102,255,1)',
                },
            ],
        }
    });

    const fetchTimeoutRef = useRef(null);
    const [endDate, setEndDate] = useState('');

    useEffect(() => {

        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }

        if (!startDate) return;
        fetchTimeoutRef.current = setTimeout(async () => {
            try {
                const data = await getIMVolatilityReport(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);

                    const lastDate = labels[labels.length - 1];
                    setEndDate(lastDate);

                    const highLowRangeValues = data.map((item) => item.highLowRange);
                    const percentileValues = data.map((item) => item.percentile);

                    setChartData({
                        highLow: {
                            labels: labels,
                            datasets: [
                                {
                                    label: 'IM主连当日最高价与最低价差',
                                    data: highLowRangeValues,
                                    fill: false,
                                    backgroundColor: 'rgba(75,192,192,0.4)',
                                    borderColor: 'rgba(75,192,192,1)',
                                }
                            ],
                        },
                        percentile: {
                            labels: labels,
                            datasets: [
                                {
                                    label: 'IM主连波动幅度百分比回测报告',
                                    data: percentileValues,
                                    fill: false,
                                    backgroundColor: 'rgba(75,192,192,0.4)',
                                    borderColor: 'rgba(75,192,192,1)',
                                }
                            ],
                        }
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

    const getChartOptions = (chartData, startDate, endDate, showPointsDetail) => ({
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
            },
            annotation: {
                annotations: {
                    timeRangeLabel: {
                        type: 'label',
                        xValue: chartData.labels[Math.floor(chartData.labels.length * 0.07)],
                        yValue: Math.max(...chartData.datasets[0].data) * 0.99,
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
    });

    return (
        <div>
            <h2>{TITLE}</h2>
            <div style={{ marginTop: '10px', fontSize: '20px', fontWeight: 'bold' }}>
                最新值：{chartData.highLow.datasets[0]?.data[chartData.highLow.datasets[0]?.data.length - 1]?.toFixed(2) || '-'} （{endDate}）
            </div>
            <Line
                data={chartData.highLow}
                options={getChartOptions(chartData.highLow, startDate, endDate, showPointsDetail)}
            />
            <div style={{ marginTop: '30px', fontSize: '20px', fontWeight: 'bold' }}>
                分位数最新值：{chartData.percentile.datasets[0]?.data[chartData.percentile.datasets[0]?.data.length - 1]?.toFixed(2) || '-'} （{endDate}）
            </div>
            <Line
                data={chartData.percentile}
                options={getChartOptions(chartData.percentile, startDate, endDate, showPointsDetail)}
            />
        </div>

    );
};

export default IMVolatilityReportChart;