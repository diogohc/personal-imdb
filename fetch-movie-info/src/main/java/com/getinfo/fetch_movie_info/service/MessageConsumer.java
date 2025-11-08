package com.getinfo.fetch_movie_info.service;


import com.getinfo.fetch_movie_info.dto.MovieDto;
import com.getinfo.fetch_movie_info.entity.Movie;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Log4j
public class MessageConsumer {

    private final GetMovieInfoService getMovieInfoService;
    private final MovieService movieService;

    public MessageConsumer(GetMovieInfoService getMovieInfoService, MovieService movieService){
        this.getMovieInfoService = getMovieInfoService;
        this.movieService = movieService;
    }

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    @RabbitListener(queues = "#{@queueName}")
    public void receiveMessage(String message){
        log.info(String.format("Message [%s] received from queue", message));

        //get movie info
        MovieDto movieDto = getMovieInfoService.getMovie(message);

        if(movieDto != null){
            if(movieDto.imdbRating == null  || movieDto.imdbRating.equals("N/A")){
                movieDto.imdbRating = "0";
            }
            Movie m = new Movie(movieDto.Title, Integer.parseInt(movieDto.Year), movieDto.Plot, movieDto.Director, movieDto.Writer, movieDto.Country,
                    movieDto.Poster, movieDto.imdbID, Integer.parseInt(movieDto.Runtime.split(" ")[0]), Float.parseFloat(movieDto.imdbRating), movieDto.Genre,
                    new Timestamp(System.currentTimeMillis())
            );

            movieService.saveMovie(m);
        }else{
            log.error(String.format("Movie with imdbId %s could not be saved", message));
        }
    }
}
