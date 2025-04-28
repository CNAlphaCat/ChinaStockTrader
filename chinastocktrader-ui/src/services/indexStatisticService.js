import axios from 'axios';

const CSI1000DivideCSI300_URL = '/api/index/statistic/CSI1000DivideCSI300/';

export const getCSI1000DivideCSI300 = async (startDate) => {
  try {
    const response = await axios.get(CSI1000DivideCSI300_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};
