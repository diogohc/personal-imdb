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
    @DeleteMapping("/deleteReview/{reviewId}")
    public ResponseEntity<?> deleteReview(@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable(name="reviewId") Long reviewId){

        Long userId = jwtService.extractUserId(authorizationHeader);
        if(userId == -1){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        log.info("[DELETE] - Delete review with id= {} by user {}",reviewId, userId);
        return reviewService.deleteReview(reviewId);
    }


    @Operation(summary = "Edit a review")
    @PutMapping("/editReview")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto reviewDto){
        log.info("[PUT] - Edit review. Review Dto: " +reviewDto);

        return reviewService.editReview(reviewDto);
    }

    @Operation(summary = "List user's reviews")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listUserReviews(@RequestHeader("Authorization") String authorizationHeader,
                                             @PathVariable(name = "userId") Long userId,
                                             @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(defaultValue = "date_added") String sortBy, @RequestParam(defaultValue = "desc") String ascOrDesc){
        Long id = jwtService.extractUserId(authorizationHeader);

        if(id == -1){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<MovieDto> userReviews = reviewService.listUserReviewsPaginatedByUserId(userId, page, pageSize, sortBy, ascOrDesc);
        log.info("[GET] - Get reviews for user with id {} by user: {}", userId, id);
        return new ResponseEntity<Object>(userReviews, HttpStatus.OK);
    }
}
