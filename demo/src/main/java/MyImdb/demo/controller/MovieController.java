package MyImdb.demo.controller;

import MyImdb.demo.config.JwtService;
import MyImdb.demo.model.User;
import MyImdb.demo.service.MovieService;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/addMovie/{imdb_id}")
    public ResponseEntity<?> addMovie(@PathVariable(name="imdb_id") String imdb_id) throws JsonProcessingException, JSONException {
        log.info("[POST] - Add movie");
        return movieService.addMovie(imdb_id);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllMovies(@RequestParam String filter, @RequestParam int offset, @RequestParam int pageSize){
        try {
            log.info("[GET] - Get all movies ");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return new ResponseEntity<Object>(movieService.getAllMovies(username, filter, offset, pageSize), HttpStatus.OK);
        } catch(io.jsonwebtoken.ExpiredJwtException e){
            return new ResponseEntity<Object>("Token expired", HttpStatus.FORBIDDEN);
        }
    }
//TODO EXPERIMENTAR ENDPOINT ACIMA
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(name="id") int id) throws JsonProcessingException {
        log.info("[GET] - Get movie with the id: {}", id);
        /*String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getUserIdWithUsername(username);
        if(userId == -1){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }*/
        return movieService.getMovieById(id);
    }

}
