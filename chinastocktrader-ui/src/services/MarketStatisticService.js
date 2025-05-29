import axios from 'axios';

const STOCK_LIMIT_SUMMARY_URL = '/api/market/statistic/stockLimitSummary';

export const getStockLimitSummary = async () => {
  try {
    const response = await axios.get(STOCK_LIMIT_SUMMARY_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};