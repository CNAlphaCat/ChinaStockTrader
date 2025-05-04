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
import { getStockLimitSummary } from '../../services/marketStatisticService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const TITLE= '涨跌停情绪对数比率指数'

const StockLimitLogOddsChart = ({ showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: TITLE,
                data: [],
                fill: false,
                backgroundColor: 'rgba(75,192,192,0.4)',
                borderColor: 'rgba(75,192,192,1)',
            },
        ],
    });

    const fetchTimeoutRef = useRef(null);

    useEffect(() => {
        if (fetchTimeoutRef.current) {
            clearTimeout(fetchTimeoutRef.current);
        }
        fetchTimeoutRef.current = setTimeout(async () => {
            try {
                const data = await getStockLimitSummary();

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.tradeDate);
                    const sentimentScore = data.map((item) => item.sentimentScore);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: TITLE,
                                data: sentimentScore,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
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
    }, []);

    return (
        <div>
            <h2>{TITLE}</h2>
            <div style={{ marginBottom: '20px', fontSize: '16px' }}>

            <div style={{
                textAlign: 'center',
                padding: '10px',
                backgroundColor: '#f9f9f9',
                borderRadius: '5px',
                display: 'inline-block',
                margin: '10px auto'
            }}>
                <code>
                    sentimentScore = log<sub>e</sub>[(涨停家数 + ε) / (跌停家数 + ε)]
                </code>
            </div>
            <p style={{ marginTop: '10px', fontStyle: 'italic' }}>
                其中 ε = 0.1，用于防止除零错误。
            </p>
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

export default StockLimitLogOddsChart;
