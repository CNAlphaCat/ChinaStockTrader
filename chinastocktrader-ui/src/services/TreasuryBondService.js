import axios from 'axios';

const TREASURY_BOND_DATA_URL = '/api/treasurybond/getData/';

export const getTreasuryBondData = async (startDate) => {
  try {
    const response = await axios.get(TREASURY_BOND_DATA_URL + startDate);
    return response.data;
  } catch (error) {
    console.error('Error fetching one percent volatility funds:', error);
    throw error;
  }
};
