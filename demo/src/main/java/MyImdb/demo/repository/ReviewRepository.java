package MyImdb.demo.repository;

import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.context.annotation.ApplicationScope;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@ApplicationScope
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value= "select r from Review r where r.user.id= ?1")
    List<Review> getReviewsByUserId(int userId, Sort sort);

    @Query(value= "select r from Review r where r.movie.id= ?1 and r.user.id= ?2")
    Optional<Review> findReviewByMovieIdAndUserId(int movieId, int userId);


    @Modifying
    @Query("update Review r set r.rating = ?1, r.date_added=?2 where r.id=?3")
    int updateReviewRating(int rating, Timestamp timestamp, int reviewId);

    @Query(value= "select count(r) from Review r where r.user.id= ?1")
    int nrMoviesWatched(long userId);

    @Query(value = "select SUM(m.runtime) from Movie m inner join Review r on r.movie.id=m.id where r.user.id=?1")
    int minutesMoviesWatched(long userId);


    @Query(value = "select r from Review r INNER JOIN Movie m ON r.movie.id = m.id  where r.user.id= ?1 order by ?2")
    Page<Review> getReviewsByUserIdWithPagination(int userId, String orderBy, Pageable pageable);
}
