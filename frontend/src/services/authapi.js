import axios from 'axios';

const token = ""
const BASE_URL = "http://localhost:8080/api/v1/auth"

export const register = async (formData) => {
    const response = await axios.post(`${BASE_URL}/register`, {
      username: formData.username,
      password: formData.password
    });

    return response;
  };



export const loginUser = async (formData) => {
  try {
    const response = await axios.post(`${BASE_URL}/authenticate`, {
      username: formData.username,
      password: formData.password
    });
  
    return response;
  } catch(error){
    if (error.response) {
      // Server responded with a status other than 2xx
      return {
        status: error.response.status,
        data: error.response.data
      };
    }
  }
};