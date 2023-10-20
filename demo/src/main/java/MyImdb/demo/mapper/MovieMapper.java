package MyImdb.demo.mapper;

import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.model.Movie;

public class MovieMapper {
    public static MovieDto mapToMovieDto(Movie movie, int rating){
        return new MovieDto(Math.toIntExact(movie.getId()), movie.getTitle(), movie.getPoster(), rating);
    }
}
