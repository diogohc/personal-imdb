import axios from 'axios';

const token = ""
const BASE_URL = "http://localhost:8080/api/v1/reviews"

export const getReviews = async () => {
    const token = localStorage.getItem('accessToken')

    const response = await axios.get(`${BASE_URL}/user/1`, {
        headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
        }
    });
    return response.data;
}


