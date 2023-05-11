package MyImdb.demo.repository;


import MyImdb.demo.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.Optional;

@ApplicationScope
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m from Movie m where m.imdbId = ?1")
    Optional<Movie> findByImdbId(String imdbId);

}
