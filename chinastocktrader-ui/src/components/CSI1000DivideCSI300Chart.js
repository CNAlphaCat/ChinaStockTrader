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
import annotationPlugin from 'chartjs-plugin-annotation';
import { getCSI1000DivideCSI300 } from '../services/indexStatisticService';

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

const CSI1000DivideCSI300Chart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: '中证1000/中证300 指数比值',
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
                const data = await getCSI1000DivideCSI300(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.date);
                    const values = data.map((item) => item.dividedValue);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: '中证1000/中证300 指数比值',
                                data: values,
                                fill: false,
                                backgroundColor: 'rgba(153,102,255,0.4)',
                                borderColor: 'rgba(153,102,255,1)',
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
            <h2>中证1000/中证300 指数比值</h2>
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
                            text: '中证1000/中证300 指数比值',
                            font: {
                                size: 20,
                            },
                        }, annotation: {
                            annotations: {
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