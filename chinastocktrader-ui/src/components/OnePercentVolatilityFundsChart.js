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

const OnePercentVolatilityFundsChart = ({startDate}) => {
  const [chartData, setChartData] = useState({
    labels: [],
    datasets: [
      {
        label: '上证指数 1% 波动率资金量(亿)',
        data: [],
        fill: false,
        backgroundColor: 'rgba(75,192,192,0.4)',
        borderColor: 'rgba(75,192,192,1)',
      },
    ],
  });

  useEffect(() => {
    const fetchChartData = async () => {
      if (!startDate) return; 
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
                  label: '上证指数 1% 波动率资金量(亿)',
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
  }, [startDate]);

  return (
    <div>
      <h2>上证指数1%波动率所需资金量（亿）</h2>
      <Line data={chartData} />
    </div>
  );
};

export default OnePercentVolatilityFundsChart;
