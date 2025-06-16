import axios from 'axios';

const DIFF_BEWTEEN_IM_AND_INDEX_URL = '/api/future/getDiffBetweenIMAndIndex';

export const getDiffBetweenIMAndIndex = async (startYear, startMonth) => {
    try {
        const response = await axios.post(DIFF_BEWTEEN_IM_AND_INDEX_URL, {
            startYear,
            startMonth
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching one percent volatility funds:', error);
        throw error;
    }
};