import axios from 'axios';

const CSI1000DivideCSI300_URL = '/api/index/statistic/CSI1000DivideCSI300/';
const EQUITY_PREMIUMINDEX_URL =  "/api/index/statistic/equitypremiumindex/";

export const getCSI1000DivideCSI300 = async (startDate) => {
  try {
    const response = await axios.get(CSI1000DivideCSI300_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};


export const getEquityPremiumIndex = async (startDate) => {
  try {
    const response = await axios.get(EQUITY_PREMIUMINDEX_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};