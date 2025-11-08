package com.getinfo.fetch_movie_info.service;

import com.getinfo.fetch_movie_info.entity.Movie;
import com.getinfo.fetch_movie_info.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class MovieService {
    private final Logger logger = Logger.getLogger(MovieService.class.getName());
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public void saveMovie(Movie movie){
        movieRepository.save(movie);
        logger.info(String.format("Movie with title %s has been saved", movie.title));

    }

}
