import axios from 'axios';

const IM_VOLATILITY_REPORT_URL = '/api/report/IMVolatilityReport/';

export const getIMVolatilityReport = async (startDate) => {
  try {
    const response = await axios.get(IM_VOLATILITY_REPORT_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error getIMVolatilityReport:', error);
    throw error;
  }
};