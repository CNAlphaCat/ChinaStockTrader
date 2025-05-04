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
import { getCSI1000DivideCSI300 } from '../../services/indexStatisticService';

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

const TITLE = '中证1000/中证300 指数比值';

const CSI1000DivideCSI300Chart = ({ startDate, showPointsDetail = true }) => {
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

    useEffect(() => {

        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }

        if (!startDate) return;
        fetchTimeoutRef.current = setTimeout(async () => {
            try {
                const data = await getCSI1000DivideCSI300(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);

                    const lastDate = labels[labels.length - 1];
                    setEndDate(lastDate);

                    const values = data.map((item) => item.dividedValue);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: TITLE,
                                data: values,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
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
                                    yValue: Math.max(...chartData.datasets[0].data) * 0.99,
                                    backgroundColor: 'rgba(255,255,255,0.7)',
                                    borderWidth: 1,
                                    borderColor: 'gray',
                                    content: [` ${startDate} - ${endDate}`],
                                    font: {
                                        size: 13,
                                    },
                                    padding: 6,
                                },
                                line_if_policy: {
                                    type: 'line',
                                    yMin: 1.62,
                                    yMax: 1.62,
                                    borderColor: 'red',
                                    borderWidth: 2,
                                    borderDash: [5, 5],
                                    label: {
                                        enabled: false,
                                        content: '80%',
                                    }
                                },
                                line_im_policy: {
                                    type: 'line',
                                    yMin: 1.42,
                                    yMax: 1.42,
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
                }}
            />

        </div>

    );
};

export default CSI1000DivideCSI300Chart;