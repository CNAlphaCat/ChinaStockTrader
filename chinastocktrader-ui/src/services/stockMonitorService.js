import axios from 'axios';

const STOCK_MONITOR_URL = '/api/stock/monitor';

export const monitorStock = async (stockList) => {
  try {
    const response = await axios.post(STOCK_MONITOR_URL, stockList, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
    return response.data;
  } catch (error) {
    console.error('Error fetching stock monitor:', error);
    throw error;
  }
};
