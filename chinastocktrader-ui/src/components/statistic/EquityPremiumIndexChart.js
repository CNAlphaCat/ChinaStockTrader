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

import { getEquityPremiumIndex } from '../../services/indexStatisticService';

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
                    const percentile = data.map((item) => item.percentile);

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

    return (
        <div>
            <h2>股权溢价指数</h2>
            <Line
                data={chartData}
                options={{
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
                            text: '股权溢价指数历史百分位（当前值占历史数据的百分比）',
                            font: {
                                size: 20,
                            },
                        }, annotation: {
                            annotations: {
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
                }}
            />
        </div>
    );
};

export default EquityPremiumIndexChart;