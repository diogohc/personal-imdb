package MyImdb.demo.controller;

import MyImdb.demo.config.JwtService;
import MyImdb.demo.model.User;
import MyImdb.demo.service.MovieService;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.security.PermitAll;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {
    @Autowired
    MovieService movieService = new MovieService();

    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class.getName());

    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestParam(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        logger.info("[POST] - Add movie");
        return movieService.addMovie(imdb_id);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllMovies(){
        logger.info("[GET] - Get all movies ");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<Object>(movieService.getAllMovies(username), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(name="id") int id) throws JsonProcessingException {
        logger.info("[GET] - Get movie with the id: " + id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getUserIdWithUsername(username);
        if(userId == -1){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        return movieService.getMovieById(id, userId);
    }
}
