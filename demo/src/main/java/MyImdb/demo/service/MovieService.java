package MyImdb.demo.service;

import MyImdb.demo.dto.MovieDetailDto;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.enums.AddExternalMovieStatus;
import MyImdb.demo.gson.MovieGson;
import MyImdb.demo.mapper.MovieMapper;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.utils.GetMovieData;
import MyImdb.demo.utils.UserSessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {
    public static final int STATUS_MOVIE_ALREADY_EXISTS_IN_DB = 1;
    public static final int STATUS_MOVIE_NOT_SAVED = 2;
    public static final int STATUS_MOVIE_SAVED_SUCCESSFULLY = 3;
    public static final int STATUS_INCORRECT_IMDB_ID = 4;
    public static final int STATUS_ONLY_ACCEPT_MOVIES = 5;

    private final MovieRepository movieRepository;

    private final ReviewRepository reviewRepository;



    public AddExternalMovieStatus addMovie(String imdbId) throws JSONException, JsonProcessingException {
        String[] url = {"https://www.omdbapi.com/?i=", "&apikey=a9c633d3"};
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String stringResponse="";
        GetMovieData getMovieData = new GetMovieData();
        stringResponse = getMovieData.getMovies(url[0] + imdbId + url[1]);

        JSONObject jsonResponse = new JSONObject(stringResponse);
        if(jsonResponse.getString("Response").equals("False")){
            return AddExternalMovieStatus.INCORRECT_IMDB_ID;
        }
        if(!jsonResponse.getString("Type").equalsIgnoreCase("movie")){
            return AddExternalMovieStatus.ONLY_ACCEPT_MOVIES;
        }
        log.info("Adding the movie with the imdb_id " + imdbId +" to the database");
        MovieGson movieGson = objectMapper.readValue(stringResponse, MovieGson.class);
        return insertMovie(movieGson);
    }

    @Transactional
    public AddExternalMovieStatus insertMovie(MovieGson movieGson){
        Movie m = new Movie(movieGson.getTitle(), Integer.parseInt(movieGson.getYear()), movieGson.getPlot(),
                movieGson.getDirector(), movieGson.getWriter(), movieGson.getCountry(), movieGson.getPoster(),
                movieGson.getImdbID(), Integer.parseInt(movieGson.getRuntime().split(" ")[0]),
                Float.parseFloat(movieGson.getImdbRating()), movieGson.getGenre(),
                new Timestamp(System.currentTimeMillis()));

        //if movie already exists in the DB
        if(movieRepository.findByImdbId(m.getImdbId()).isPresent()){
            return AddExternalMovieStatus.MOVIE_ALREADY_EXISTS_IN_DB;
        }

        movieRepository.save(m);

        if(movieRepository.existsById(m.getId())){
            return AddExternalMovieStatus.MOVIE_SAVED_SUCCESSFULLY;
        }
        return AddExternalMovieStatus.MOVIE_NOT_SAVED;
    }

    public ResponseEntity<?> getAllMovies(String username) {

        List<Movie> moviesList = movieRepository.findAll();
        List<MovieDto> movies = new ArrayList<MovieDto>();

        UserSessionData userSessionData = new UserSessionData();
        System.out.println("INSIDE MOVIE SERVICE: "+ userSessionData.getUserData().mapMovieIdRating);

        moviesList.forEach(movie -> {
            Integer rating = userSessionData.getUserData().mapMovieIdRating.get(movie.getId().intValue());
            MovieDto movieDto = new MovieDto(Math.toIntExact(movie.getId()), movie.getTitle(), movie.getPoster(), rating == null ? -1 : rating);
            movies.add(movieDto);
        });

        return new ResponseEntity<Object>(movies, HttpStatus.OK);
    }

    //TODO usar a query para ir buscar o filme + review (inner join) como esta na listagem em vez de fazer 2 gets
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

    //TODO testar no controller este metodo para ir buscar todos os filmes
    //permitir requests sem jwt ao movie controller. e invocar no postman com /userId
    //se vier o pretendido, depois fazer no controller a parte de retrieve o userID do jwt
    public List<MovieDto> getAllMovies(Long userId){
        List<Object[]> moviesWithRatings = movieRepository.findMoviesWithRatingByUserId(userId);
        List<MovieDto> lstMovies =  new ArrayList<>();

        for (Object[] movieRating : moviesWithRatings) {
            Movie movie = (Movie) movieRating[0];
            Integer rating = (Integer) movieRating[1];
            lstMovies.add(MovieMapper.mapToMovieDto(movie, rating));
        }
        return lstMovies;
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
