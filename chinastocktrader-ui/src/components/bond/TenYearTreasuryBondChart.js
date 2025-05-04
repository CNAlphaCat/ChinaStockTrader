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
import { getTreasuryBondData } from '../../services/treasuryBondService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const TITLE = '中美十年期国债收益率';

const TenYearUSTreasuryBondChart = ({ startDate, showPointsDetail = true }) => {
    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: '中国十年期国债收益率',
                data: [],
                fill: false,
                backgroundColor: 'rgba(153,102,255,0.4)',
                borderColor: 'rgba(153,102,255,1)',
            }, {
                label: '美国十年期国债收益率',
                data: [],
                fill: false,
                backgroundColor: 'rgba(75,192,192,0.4)',
                borderColor: 'rgba(75,192,192,1)',
                spanGaps: true,
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
                const data = await getTreasuryBondData(startDate);

                if (Array.isArray(data)) {
                    const labels = data.map((item) => item.solarDate);
                    const chinaTenYearTreasuryBondYield = data.map((item) => item.tenYearTreasuryBondYield);
                    const usTenYearTreasuryBondYield = data.map((item) => item.tenYearUSTreasuryBondYield);

                    setChartData({
                        labels: labels,
                        datasets: [
                            {
                                label: '中国十年期国债收益率',
                                data: chinaTenYearTreasuryBondYield,
                                fill: false,
                                backgroundColor: 'rgba(153,102,255,0.4)',
                                borderColor: 'rgba(153,102,255,1)',
                                spanGaps: true
                            }, {
                                label: '美国十年期国债收益率',
                                data: usTenYearTreasuryBondYield,
                                fill: false,
                                backgroundColor: 'rgba(75,192,192,0.4)',
                                borderColor: 'rgba(75,192,192,1)',
                                spanGaps: true,
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

export default TenYearUSTreasuryBondChart;