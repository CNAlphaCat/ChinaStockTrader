import axios from 'axios';

const DIFF_BEWTEEN_IM_AND_INDEX_URL = '/api/future/getDiffBetweenIMAndIndex';
const DIFF_BEWTEEN_IF_AND_INDEX_URL = '/api/future/getDiffBetweenIFAndIndex';

export const getDiffBetweenIMAndIndex = async (startYear, startMonth) => {
    try {
        const response = await axios.post(DIFF_BEWTEEN_IM_AND_INDEX_URL, {
            startYear,
            startMonth
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching getDiffBetweenIMAndIndex:', error);
        throw error;
    }
};


export const getDiffBetweenIFAndIndex = async (startYear, startMonth) => {
    try {
        const response = await axios.post(DIFF_BEWTEEN_IF_AND_INDEX_URL, {
            startYear,
            startMonth
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching getDiffBetweenIFAndIndex:', error);
        throw error;
    }
}