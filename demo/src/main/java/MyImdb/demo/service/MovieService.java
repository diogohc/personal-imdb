package MyImdb.demo.service;

import MyImdb.demo.dto.MovieDetailDto;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.mapper.MovieMapper;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.utils.UserSessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    private final ReviewRepository reviewRepository;

    private final RabbitMQProducer rabbitMQProducer;

    @Value("${API_KEY}")
    String apiKey;


    public String addMovie(String imdbId) throws JSONException, JsonProcessingException {

        if(movieRepository.findByImdbId(imdbId).isEmpty()) {
            rabbitMQProducer.sendMessage(imdbId);
            return "Movie will be inserted shortly";
        }

        return "Movie already exists";
    }


    //deprecated
    public ResponseEntity<?> getAllMovies(String username) {

        List<Movie> moviesList = movieRepository.findAll();
        List<MovieDto> movies = new ArrayList<MovieDto>();

        UserSessionData userSessionData = new UserSessionData();

        moviesList.forEach(movie -> {
            Integer rating = userSessionData.getUserData().mapMovieIdRating.get(movie.getId().intValue());
            MovieDto movieDto = new MovieDto(Math.toIntExact(movie.getId()), movie.getTitle(), movie.getPoster(), rating == null ? -1 : rating, movie.getYear());
            movies.add(movieDto);
        });

        return new ResponseEntity<Object>(movies, HttpStatus.OK);
    }

    //TODO use a query to fetch movie + review (inner join) like it is on the list instead of doing 2 database selects
    public ResponseEntity<?> getMovieById(int id, int userId) throws JsonProcessingException {
        int userRating = -1;
        Optional<Movie> m = movieRepository.findById((long) id);
        Optional<Review> review = reviewRepository.findReviewByMovieIdAndUserId(id, userId);
        if(review.isPresent()){
            userRating = review.get().getRating();
        }
        if(m.isPresent()){
            Movie movie = m.get();
            MovieDetailDto movieDetailDto = new MovieDetailDto(movie.getId().intValue(), movie.getTitle(), movie.getYear(), movie.getPlot(), movie.getDirector(), movie.getWriter(),
                    movie.getCountry(), movie.getPoster(), movie.getRuntime(), movie.getImdbRating(), userRating);
            return new ResponseEntity<Object>(movieDetailDto, HttpStatus.OK);
        }
        return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
    }


    public Movie getMovieByImdbID(String imdbID){
        Optional<Movie> movie = movieRepository.findByImdbId(imdbID);

        return movie.orElse(null);
    }

    public List<MovieDto> getMoviesWithUserRatingsPaginated(Long userId, int page, int pageSize, String sortBy, String ascOrDesc) {

        Sort.Direction direction = ascOrDesc.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Page<Object[]> pagesMoviesWithRatings = movieRepository.findMoviesWithUserRatings(userId, pageable);
        List<Object[]> lstMoviesWithRatings = pagesMoviesWithRatings.getContent();

        List<MovieDto> lstMovies =  new ArrayList<>();

        for (Object[] movieRating : lstMoviesWithRatings) {
            Movie movie = (Movie) movieRating[0];
            Integer rating = (Integer) (movieRating[1] == null ? 0 : movieRating[1]);
            lstMovies.add(MovieMapper.mapToMovieDto(movie, rating));
        }

        return lstMovies;
    }
}
