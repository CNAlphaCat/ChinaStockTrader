import axios from 'axios';

const ONE_PERCENT_VOLATILITY_FUNDS_URL = '/api/sse/onePercentVolatilityFunds/';

export const getOnePercentVolatilityFunds = async (startDate) => {
  try {
    const response = await axios.get(ONE_PERCENT_VOLATILITY_FUNDS_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};
