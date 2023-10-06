package MyImdb.demo.controller;


import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
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


    @Operation(summary = "Add new review")
    @PostMapping("/addReview")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto reviewdto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.insertReview(username, reviewdto);
    }

    @Operation(summary = "Delete a review")
    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam(name="movieId") int movieId, @RequestParam(name="userId") int userId){
        log.info("[DELETE] - Delete review with movieId= "+movieId+" and userId= " +userId);
        return reviewService.deleteReview(movieId, userId);
    }

/*    @Operation(summary = "List user's reviews")
    @GetMapping("")
    public ResponseEntity<?> listUserReviews() throws JSONException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Vector<ReviewDto> reviews = reviewService.listUserReviews(username);
        log.info("[GET] - Get all user reviews");
        if(reviews!= null && reviews.size() > 0){
            return new ResponseEntity<Object>(reviews, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }*/

    @Operation(summary = "Edit a review")
    @PutMapping("/editReview")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto reviewDto){
        log.info("[PUT] - Edit review. Review Dto: " +reviewDto);

        return reviewService.editReview(reviewDto);
    }

    @Operation(summary = "List user's reviews")
    @GetMapping("")
    public ResponseEntity<?> listUserReviews(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(defaultValue = "date_added") String sortBy, @RequestParam(defaultValue = "desc") String ascOrDesc){
        Long userId = jwtService.extractUserId(authorizationHeader);
        List<MovieDto> userReviews = reviewService.listUserReviewsPaginatedByUserId(userId, page, pageSize, sortBy, ascOrDesc);
        log.info("[GET] - Get reviews for user with id {}", userId);
        return new ResponseEntity<Object>(userReviews, HttpStatus.OK);
    }
}
