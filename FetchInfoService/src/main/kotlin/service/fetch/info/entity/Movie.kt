package service.fetch.info.entity

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.sql.Timestamp


@Entity
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class Movie(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var title: String,
    var year: Int,
    var plot: String,
    var director: String,
    var writer: String,
    var country: String,
    var poster: String,
    @Column(unique=true)
    var imdbId: String,
    var runtime: Int,
    var imdbRating: Float,
    var genre: String,
    var dt_created: Timestamp
){

    constructor(title: String, year: Int, plot: String, director: String, writer: String, country: String, poster: String,
    imdbId: String, runtime: Int, imdbRating: Float, genre: String, dt_created: Timestamp) :
            this(null, title, year, plot, director, writer, country, poster,imdbId, runtime, imdbRating,genre
            ,dt_created) {

    }
}