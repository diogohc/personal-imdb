import axios from 'axios';

const BASE_URL = "http://localhost:8080/api/v1/movies"

export const getMovies = async () => {
  const token = localStorage.getItem('accessToken')
    const response = await axios.get(`${BASE_URL}/all-movies/user/1`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
  
    return response.data;
  };

/*
export const searchMovies = async(query) => {
    const response = await fetch(`${BASE_URL}/all-movies/user/1`)
}
    */