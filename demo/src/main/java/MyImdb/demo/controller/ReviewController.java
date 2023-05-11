package MyImdb.demo.controller;

import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.service.ReviewService;
import MyImdb.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.Vector;


@RestController
@RequestMapping("/api/v1/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping("/addReview")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto reviewdto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.insertReview(username, reviewdto);
    }

    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam(name="movieId") int movieId, @RequestParam(name="userId") int userId){
        log.info("[DELETE] - Delete review with movieId= "+movieId+" and userId= " +userId);
        return reviewService.deleteReview(movieId, userId);
    }

    @GetMapping("")
    public ResponseEntity<?> listUserReviews(@RequestParam String filter, @RequestParam int offset, @RequestParam int pageSize) throws JSONException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Vector<ReviewDto> reviews = reviewService.listUserReviewsWithFilterAndPagination(username, filter, offset, pageSize);
        log.info("[GET] - Get all user reviews");
        if(reviews!= null && reviews.size() > 0){
            return new ResponseEntity<Object>(reviews, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/editReview")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto reviewDto){
        log.info("[PUT] - Edit review. Review Dto: " +reviewDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.editReview(username, reviewDto);
    }
}
