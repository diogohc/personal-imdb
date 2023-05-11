package MyImdb.demo.service;

import MyImdb.demo.dto.MovieDetailDto;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.gson.MovieGson;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.utils.GetData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final MovieRepository movieRepository;

    private final ReviewRepository reviewRepository;

    @Autowired
    ReviewService reviewService;
    //TODO solucao para ordenar reviews por nome de filme, sol1-> join em sql (dava erro) sol2-> adicionar coluna com movietitle na tabela de reviews
    public ResponseEntity<?> addMovie(String imdbId) throws JSONException, JsonProcessingException {
        String[] url = {"https://www.omdbapi.com/?i=", "&apikey=a9c633d3"};
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

        if(movieRepository.findByImdbId(imdbId).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Movie already exists in the database");
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

    public List<MovieDto> getAllMovies(String username, String filter, int offset, int pageSize) {
        String orderBy = filter.split(",")[0].strip();
        String ascOrDesc = filter.split(",")[1].strip();
        List<Movie> moviesList = null;
        Page<Movie> moviesListPage = null;

        if(ascOrDesc.equalsIgnoreCase("asc")){
            moviesListPage = movieRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(orderBy).ascending()));
        } else {
            moviesListPage = movieRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(orderBy).descending()));
        }
        //get list of movies from page
        moviesList = moviesListPage.getContent();

        List<MovieDto> movies = new ArrayList<MovieDto>();

        HashMap<Integer, Integer> hmMovieIdRating = new HashMap<>();
        //Get user reviews. If the user rated the movie, include its value
        Vector<ReviewDto> userReviews= reviewService.listUserReviews(username);
        if(userReviews!=null){
            for(ReviewDto reviewDto: userReviews){
                hmMovieIdRating.put((int) reviewDto.getMovieId(), reviewDto.getRating());
            }
        }

        for(Movie movie: moviesList){
            Integer rating = hmMovieIdRating.get(movie.getId().intValue());
            MovieDto movieDto = new MovieDto(Math.toIntExact(movie.getId()), movie.getTitle(), movie.getPoster(), rating==null ? -1 : rating);
            movies.add(movieDto);
        }

        return movies;
    }

    public ResponseEntity<?> getMovieById(int id) throws JsonProcessingException {
        int userRating = -1;
        Optional<Movie> m = movieRepository.findById((long) id);
        Optional<Review> review = reviewRepository.findReviewByMovieIdAndUserId(id, -1);
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

}
