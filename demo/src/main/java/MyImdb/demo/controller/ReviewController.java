package MyImdb.demo.controller;


import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.entity.Review;
import MyImdb.demo.service.ReviewService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Vector;


@RestController
@RequestMapping("/api/v1/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private final JwtService jwtService;

    @PostMapping("/addReview")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto reviewdto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[POST] - Add review. Review Dto: " + reviewdto);
        return reviewService.insertReview(username, reviewdto);
    }

    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam(name="movieId") Long movieId, @RequestParam(name="userId") Long userId){
        log.info("[DELETE] - Delete review with movieId= "+movieId+" and userId= " +userId);
        return reviewService.deleteReview(movieId, userId);
    }

    @GetMapping("")
    public ResponseEntity<?> listUserReviews(@RequestHeader("Authorization") String authorizationHeader) throws JSONException {
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = jwtService.extractUserId(authorizationHeader.substring("Bearer ".length()));

        Vector<ReviewDto> reviews = reviewService.listUserReviews(userId);
        log.info("[GET] - Get all user reviews for user " + userId);

        return new ResponseEntity<Object>(reviews, HttpStatus.OK);

    }

    @GetMapping("/user-reviews/{userId}")
    public ResponseEntity<?> listUserReviews(@PathVariable("userId") Long userId) throws JSONException {
        Vector<ReviewDto> reviews = reviewService.listUserReviews(userId);
        log.info("[GET] - Get all user reviews for user " + userId);

        return new ResponseEntity<Object>(reviews, HttpStatus.OK);


    }

    @PutMapping("/editReview")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto reviewDto){
        log.info("[PUT] - Edit review. Review Dto: " +reviewDto);

        return reviewService.editReview(reviewDto);
    }


    //GET USER REVIEWS UPDATED ENDPOINT
    //RETURNS THE REVIEW OBJECT AND INSIDE HAS THE MOVIE OBJECT COMPLETED
    @GetMapping("/movies-with-reviews/{userId}")
    public ResponseEntity<?> getMoviesReviewedByUser(@PathVariable("userId") Long userId) {
        List<Review> lstMovies = reviewService.getAllMoviesReviewedByUser(userId);

        return new ResponseEntity<>(lstMovies, HttpStatus.OK);
    }
}
