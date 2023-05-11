package MyImdb.demo.service;

import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.MovieRepository;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Vector;


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
            Review review = new Review(user.get(), movie.get(), reviewdto.getRating(), reviewdto.getDateAdded(), movie.get().getTitle());
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

    public Vector<ReviewDto> listUserReviewsWithFilterAndPagination(String username, String filter, int offset, int pageSize){
        String orderBy = filter.split(",")[0].strip();
        String ascOrDesc = filter.split(",")[1].strip();


        List<Review> reviewsList = null;
        Page<Review> reviewsListPage = null;

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {

            if(ascOrDesc.equalsIgnoreCase("asc")){
                reviewsListPage = reviewRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(orderBy).ascending()));
            } else {
                reviewsListPage = reviewRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(orderBy).descending()));
            }

            //get list of reviews from reviewsListPage
            reviewsList = reviewsListPage.getContent();

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
