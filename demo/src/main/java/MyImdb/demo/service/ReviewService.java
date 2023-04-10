package MyImdb.demo.service;

import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.gson.MovieGson;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;


@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserService userService;

    @Transactional
    public ResponseEntity<?> insertReview(String username, ReviewDto reviewdto){
        int userId = userService.getUserIdWithUsername(username);
        Optional<Movie> movie = movieRepository.findById((long) reviewdto.getMovieId());
        Optional<User> user = userRepository.findById((long) userId);

        if(user.isPresent() && movie.isPresent()){
            Review review = new Review(user.get(), movie.get(), reviewdto.getRating(), reviewdto.getDateAdded());
            reviewRepository.save(review);
            if(reviewRepository.existsById(review.getId())){
                return ResponseEntity.status(HttpStatus.CREATED).body(review);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> deleteReview(int movieId, int userId) {
        //todo editar query. TESTAR ANTES
        Optional<Review> rev = reviewRepository.findReviewByMovieIdAndUserId(movieId, userId);
        if(rev.isPresent()){
            reviewRepository.deleteById(rev.get().getId());
            return ResponseEntity.status(HttpStatus.OK).build();

        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    public Vector<ReviewDto> listUserReviews(String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {

            List<Review> reviewsList = reviewRepository.getReviewsByUserId(user.get().getId().intValue(), Sort.by("date_added"));
            Vector<ReviewDto> reviews = new Vector<>();

            for (Review review : reviewsList) {
                ReviewDto reviewDto = new ReviewDto(review.getId(), review.getMovie().getId(), review.getRating(), review.getMovie().getPoster(), review.getMovie().getTitle(),
                        review.getDate_added());
                reviews.add(reviewDto);

            }
            return reviews;
        }
        return null;
    }

    @Transactional
    public ResponseEntity<?> editReview(String username, ReviewDto reviewDto){
        int nrRecordsUpdated;

        nrRecordsUpdated = reviewRepository.updateReviewRating(reviewDto.getRating(), reviewDto.getDateAdded(), (int) reviewDto.getId());
        if (nrRecordsUpdated==1) {
            Optional<Review> updatedReview = reviewRepository.findById((long) reviewDto.getId());
            if(updatedReview.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(reviewDto);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("not updated");
    }
}
