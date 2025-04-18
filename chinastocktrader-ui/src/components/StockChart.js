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
import { getOnePercentVolatilityFunds } from '../services/sseIndexHistoryService';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
  );

const StockChart = () => {
  const [chartData, setChartData] = useState({
    labels: [],
    datasets: [
      {
        label: 'Shanghai Index History',
        data: [],
        fill: false,
        backgroundColor: 'rgba(75,192,192,0.4)',
        borderColor: 'rgba(75,192,192,1)',
      },
    ],
  });

  useEffect(() => {
    const fetchChartData = async () => {
        const startDate = '2025-01-01';
        try {
          const data = await getOnePercentVolatilityFunds(startDate);
    
          if (Array.isArray(data)) {
            console.log('Data:', data);
            const labels = data.map((item) => item.date);
            const values = data.map((item) => item.displayFunds);
    
            setChartData({
              labels: labels,
              datasets: [
                {
                  label: 'Shanghai Index History',
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
      };

    fetchChartData();
  }, []);

  return (
    <div>
      <h2>上证指数1%波动率所需资金量（亿）</h2>
      <Line data={chartData} />
    </div>
  );
};

export default StockChart;
