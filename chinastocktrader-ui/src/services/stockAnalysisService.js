import axios from 'axios';

const FIVE_MINUTES_KLINE_ANALYZE_URL = '/api/stock/analyze/kline/5minutes/';

export const getFiveMinutesKlineAnalysis = async (stockCode) => {
  try {
    const response = await axios.get(FIVE_MINUTES_KLINE_ANALYZE_URL + stockCode);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};