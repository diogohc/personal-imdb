package MyImdb.demo.controller;

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


@RestController
@Slf4j
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@PermitAll
public class MovieController {
    private final MovieService movieService;

    private final UserService userService;
    
    @Operation(summary = "Add a new movie")
    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestParam(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        log.info("[POST] - Add movie");
        AddExternalMovieStatus status = movieService.addMovie(imdb_id);
        String responseMessage = "";
        HttpStatus responseStatus = null;

        switch(status) {
            case MOVIE_ALREADY_EXISTS_IN_DB:
                responseMessage ="Movie already exists in the database";
                responseStatus = HttpStatus.BAD_REQUEST;
                break;
            case MOVIE_NOT_SAVED:
                responseMessage ="Error Saving Movie";
                responseStatus = HttpStatus.BAD_REQUEST;
                break;
            case INCORRECT_IMDB_ID:
                responseMessage ="Incorrect IMDB ID";
                responseStatus = HttpStatus.BAD_REQUEST;
                break;
            case ONLY_ACCEPT_MOVIES:
                responseMessage ="The application only accepts movie IDS";
                responseStatus = HttpStatus.BAD_REQUEST;
                break;
            default:
                responseMessage ="Movie Created";
                responseStatus = HttpStatus.OK;
        }
        return new ResponseEntity<>(responseMessage,responseStatus);
    }

    @Operation(summary = "Get list of all movies")
    @GetMapping("")
    public ResponseEntity<?> getAllMovies(){
        log.info("[GET] - Get all movies ");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return movieService.getAllMovies(username);
    }

    @Operation(summary = "Get a movie by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(name="id") int id) throws JsonProcessingException {
        log.info("[GET] - Get movie with the id: " + id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getUserIdWithUsername(username);
        if(userId == -1){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        return movieService.getMovieById(id, userId);
    }
}
