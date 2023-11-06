package service.fetch.info.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
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
    val id: Long,
    val title: String,
    val year: Int,
    val plot: String,
    val director: String,
    val writer: String,
    val country: String,
    val poster: String,
    @Column(unique=true)
    val imdbId: String,
    val runtime: Int,
    val imdbRating: Float,
    val genre: String,
    val dt_created: Timestamp
){

}