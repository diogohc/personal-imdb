package com.getinfo.fetch_movie_info.service;

import com.getinfo.fetch_movie_info.dto.MovieDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
public class GetMovieInfoService {

    @Value("${BASE_URL}")
    private String baseUrl;

    @Value("${API_KEY}")
    private String apiKey;

    public MovieDto getMovie(String imdbId){
        StringBuilder requestUrl = new StringBuilder();
        MovieDto movieJson = null;

        requestUrl.append(baseUrl).append(imdbId).append("&apikey=").append(apiKey);

        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = null;

            request = HttpRequest.newBuilder()
                    .uri(new URI(requestUrl.toString()))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            movieJson = new Gson().fromJson(response.body(), MovieDto.class);
            return movieJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
