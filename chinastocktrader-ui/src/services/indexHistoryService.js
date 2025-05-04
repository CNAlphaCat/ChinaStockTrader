import axios from 'axios';

const ONE_PERCENT_VOLATILITY_FUNDS_URL = '/api/sse/onePercentVolatilityFunds/';
const SHENZHEN_INDEX_HISTORY_URL = '/api/szci/shenzhenIndexHistory/';
const SHANGHAI_INDEX_HISTORY_URL = '/api/sse/shanghaiIndexHistory/';

export const getOnePercentVolatilityFunds = async (startDate) => {
  try {
    const response = await axios.get(ONE_PERCENT_VOLATILITY_FUNDS_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};

export const getShenzhenIndexHistory = async (startDate) => {
  try {
    const response = await axios.get(SHENZHEN_INDEX_HISTORY_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching Shenzhen index history:', error);
    throw error;
  }
};

export const getShanghaiIndexHistory = async (startDate) => {
  try {
    const response = await axios.get(SHANGHAI_INDEX_HISTORY_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching Shanghai index history:', error);
    throw error;
  }
};