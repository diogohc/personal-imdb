package MyImdb.demo.controller;

import MyImdb.demo.dto.MovieWithRating;
import MyImdb.demo.entity.Movie;
import MyImdb.demo.entity.Review;
import MyImdb.demo.response.DefaultResponse;
import MyImdb.demo.service.MovieService;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@PermitAll
public class MovieController {
    private final MovieService movieService;

    private final UserService userService;

    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestParam(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        log.info("[POST] - Add movie");
        return movieService.addMovie(imdb_id);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(name="id") Long id) {
        log.info("[GET] - Get movie with the id: " + id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.getUserIdWithUsername(username);
        if(userId == -1){
            return new ResponseEntity<>(new DefaultResponse<>("User doesn't exist with username " + username, "NOT_FOUND"), HttpStatus.NOT_FOUND);
        }

        MovieWithRating m = movieService.getMovieById(id, userId);

        if(m != null){
            return new ResponseEntity<>(m, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new DefaultResponse<>("Movie doesn't exist with id: " + id, "NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMoviesIncludingRating(@PathVariable("userId") Long userId) {
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[GET] - Get all movies ");
        List<MovieWithRating> lstMovies = movieService.getAllMovies(userId);

        return new ResponseEntity<>(lstMovies, HttpStatus.OK);
    }
}
