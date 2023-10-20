package MyImdb.demo.repository;



import MyImdb.demo.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.Optional;

@ApplicationScope
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m from Movie m where m.imdbId = ?1")
    Optional<Movie> findByImdbId(String imdbId);


    //find all the movies and if the user reviewed it, include the review as well
    @Query("SELECT m, r.rating FROM Movie m LEFT JOIN Review r ON m.id = r.movie.id AND r.user.id = ?1")
    List<Object[]> findMoviesWithRatingByUserId(Long userId);

    //get the movie + rating
    @Query("SELECT m, r.rating FROM Movie m LEFT JOIN Review r ON m.id = r.movie.id AND r.user.id = ?2 WHERE m.id = ?1")
    List<Object[]> findMovieWithRatingByMovieIdAndUserId(Long movieId, Long userId);



    @Query("SELECT m, r.rating FROM Movie m LEFT JOIN Review r ON m.id = r.movie.id AND r.user.id = ?1")
    Page<Object[]> findMoviesWithUserRatings(Long userId, Pageable pageable);

}
