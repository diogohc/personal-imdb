package MyImdb.demo.service;

import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.exception.ResourceNotFoundException;
import MyImdb.demo.mapper.ReviewMapper;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import MyImdb.demo.utils.UserSessionData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    private final UserService userService;

    @Transactional
    public ResponseEntity<?> insertReview(String username, ReviewDto reviewdto){
        int userId = userService.getUserIdWithUsername(username);
        Optional<Movie> movie = movieRepository.findById((long) reviewdto.getMovieId());
        Optional<User> user = userRepository.findById((long) userId);

        if(user.isPresent() && movie.isPresent()){
            Review review = new Review(user.get(), movie.get(), reviewdto.getRating(), reviewdto.getDateAdded() == null ? new Timestamp(System.currentTimeMillis()) : reviewdto.getDateAdded());
            reviewRepository.save(review);

            if(reviewRepository.existsById(review.getId())){
                //update the user's map with the new review
                UserSessionData userSessionData = new UserSessionData();
                userSessionData.getUserData().mapMovieIdRating.put((int) reviewdto.getMovieId(), review.getRating());

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

    public List<MovieDto> listUserReviewsByUserId(int userId){
        User user = userRepository.findById((long) userId).orElseThrow(
                () -> new ResourceNotFoundException("User doesn't exist with given id: " + userId)
        );

        List<Review> lstReviews = reviewRepository.getReviewsByUserId(userId, Sort.by("date_added"));

        return lstReviews.stream().map((review) -> ReviewMapper.mapToMovieDto(review)).collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<?> editReview(ReviewDto reviewDto){
        int nrRecordsUpdated;

        nrRecordsUpdated = reviewRepository.updateReviewRating(reviewDto.getRating(), reviewDto.getDateAdded(), (int) reviewDto.getId());
        if (nrRecordsUpdated==1) {
            Optional<Review> updatedReview = reviewRepository.findById((long) reviewDto.getId());
            if(updatedReview.isPresent()){
                //update the user's map with the new review
                UserSessionData userSessionData = new UserSessionData();
                userSessionData.getUserData().mapMovieIdRating.put((int) reviewDto.getMovieId(), reviewDto.getRating());

                return ResponseEntity.status(HttpStatus.OK).body(reviewDto);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("not updated");
    }
}
