package MyImdb.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private int year;
    private String plot;
    private String director;
    private String writer;
    private String country;
    private String poster;
    @Column(unique=true)
    private String imdbId;
    private int runtime;
    private float imdbRating;
    private String genre;
    @OneToMany(mappedBy = "movie")
    @JsonBackReference
    private List<Review> review;
    private Timestamp dt_created;

    public Movie(String title, int year, String plot, String director, String writer, String country, String poster,
                 String imdbId, int runtime, float imdbRating, String genre, Timestamp dt_created){
        this.title = title;
        this.year = year;
        this.plot = plot;
        this.director = director;
        this.writer = writer;
        this.country = country;
        this.poster = poster;
        this.imdbId = imdbId;
        this.runtime = runtime;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.dt_created = dt_created;
    }
}
