package MyImdb.demo.service;

import MyImdb.demo.dto.MovieWithRating;
import MyImdb.demo.gson.MovieGson;
import MyImdb.demo.entity.Movie;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.utils.GetData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Value("${OMDB_API_KEY}")
    private String apiKey;

    public ResponseEntity<?> addMovie(String imdbId) throws JSONException, JsonProcessingException {
        String[] url = {"https://www.omdbapi.com/?i=", "&apikey=" +apiKey};
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String stringResponse="";
        GetData getData = new GetData();
        stringResponse = getData.getMovies(url[0] + imdbId + url[1]);

        JSONObject jsonResponse = new JSONObject(stringResponse);
        if(jsonResponse.getString("Response").equals("False")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect IMDb ID.");
        }
        if(!jsonResponse.getString("Type").equalsIgnoreCase("movie")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only accept movies (for now)");
        }
        log.info("Adding the movie with the imdb_id " + imdbId +" to the database");
        MovieGson movieGson = objectMapper.readValue(stringResponse, MovieGson.class);
        return insertMovie(movieGson);
    }

    @Transactional
    public ResponseEntity<?> insertMovie(MovieGson movieGson){
        Movie m = new Movie(movieGson.getTitle(), Integer.parseInt(movieGson.getYear()), movieGson.getPlot(),
                movieGson.getDirector(), movieGson.getWriter(), movieGson.getCountry(), movieGson.getPoster(),
                movieGson.getImdbID(), Integer.parseInt(movieGson.getRuntime().split(" ")[0]),
                Float.parseFloat(movieGson.getImdbRating()), movieGson.getGenre(),
                new Timestamp(System.currentTimeMillis()));

        //if movie already exists in the DB
        if(movieRepository.findByImdbId(m.getImdbId()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Movie Already Exists");
        }

        movieRepository.save(m);

        if(movieRepository.existsById(m.getId())){
            return ResponseEntity.status(HttpStatus.CREATED).body(m);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /*
    NAO APAGAR. TEM EXEMPLO DO MAPMOVIEIDRATING QUE E POPULADO QUANDO O USER AUTENTICA
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

     */



    public MovieWithRating getMovieById(Long movieId, Long userId) {
        List<Object[]> movieRating = movieRepository.findMovieWithRatingByMovieIdAndUserId(movieId, userId);

        if (movieRating != null && movieRating.size() > 0) {
            Movie movie = (Movie) movieRating.get(0)[0];
            Integer rating = (Integer) movieRating.get(0)[1];
            return new MovieWithRating(movie, rating);
        }
        return null;
    }


    public List<MovieWithRating> getAllMovies(Long userId){
        List<Object[]> moviesWithRatings = movieRepository.findMoviesWithRatingByUserId(userId);
        List<MovieWithRating> lstMovies =  new ArrayList<>();

        for (Object[] movieRating : moviesWithRatings) {
            Movie movie = (Movie) movieRating[0];
            Integer rating = (Integer) movieRating[1];
            lstMovies.add(new MovieWithRating(movie, rating));
        }
        return lstMovies;
    }

}
