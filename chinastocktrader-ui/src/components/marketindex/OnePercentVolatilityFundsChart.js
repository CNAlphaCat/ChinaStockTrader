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
import { getOnePercentVolatilityFunds } from '../../services/indexHistoryService';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const TITLE = '上证指数1%波动率所需资金量（亿）';

const OnePercentVolatilityFundsChart = ({ startDate, showPointsDetail = true }) => {
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


    if (!startDate) return;

    fetchTimeoutRef.current = setTimeout(async () => {
      try {
        const data = await getOnePercentVolatilityFunds(startDate);

        if (Array.isArray(data)) {
          const labels = data.map((item) => item.date);
          const values = data.map((item) => item.displayFunds);

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

export default OnePercentVolatilityFundsChart;
