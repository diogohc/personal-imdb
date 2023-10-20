package MyImdb.demo.mapper;

import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;

public class ReviewMapper {
    public static ReviewDto mapToDto(Review review){
        return null;
    }

    public static MovieDto mapToMovieDto(Review review){
        Movie movie = review.getMovie();
        return new MovieDto(Math.toIntExact(movie.getId()), movie.getTitle(), movie.getPoster(), review.getRating());
    }
}
