package MyImdb.demo.controller;

import MyImdb.demo.service.MovieService;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    //private static final Logger logger = LoggerFactory.getLogger(MovieController.class.getName());

    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestParam(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        log.info("[POST] - Add movie");
        return movieService.addMovie(imdb_id);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllMovies(){
        log.info("[GET] - Get all movies ");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return movieService.getAllMovies(username);
    }

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
