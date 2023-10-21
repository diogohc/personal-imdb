package MyImdb.demo.dto;

import MyImdb.demo.model.Movie;
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