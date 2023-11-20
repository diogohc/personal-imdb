package MyImdb.demo.controller;

import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.enums.AddExternalMovieStatus;
import MyImdb.demo.service.MovieService;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
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

    private final JwtService jwtService;

    @Operation(summary = "Add a new movie")
    @PostMapping("/addMovie/{imdb_id}")
    public ResponseEntity<?> addMovie(@RequestHeader("Authorization") String authorizationHeader,
                                      @PathVariable(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        Long userId = jwtService.extractUserId(authorizationHeader);

        log.info("[POST] - Add a new movie to the database by user {}", userId);
        movieService.addMovie(imdb_id);

        return new ResponseEntity<>("",HttpStatus.OK);
    }

    @Operation(summary = "Get a movie by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(name="id") int id) throws JsonProcessingException {
        log.info("[GET] - Get movie with the id: " + id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getUserIdWithUsername(username);
        if(userId == -1){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return movieService.getMovieById(id, userId);
    }

    @Operation(summary = "Get list of all movies with pagination and sorting (asc/desc)")
    @GetMapping("/all-movies")
    public ResponseEntity<?> getMoviesPaginatedAndSorted(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String ascOrDesc){
        Long userId = jwtService.extractUserId(authorizationHeader);

        log.info("[GET] - Get all movies paginated and sorted by user {}", userId);

        List<MovieDto> lstMovies = movieService.getMoviesWithUserRatingsPaginated(userId, page, pageSize, sortBy, ascOrDesc);
        return new ResponseEntity<Object>(lstMovies, HttpStatus.OK);
    }
}
