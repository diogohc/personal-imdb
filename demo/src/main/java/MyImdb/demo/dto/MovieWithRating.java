package MyImdb.demo.dto;

import MyImdb.demo.entity.Movie;
import MyImdb.demo.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithRating {
    private Movie movie;
    private Integer rating;

}