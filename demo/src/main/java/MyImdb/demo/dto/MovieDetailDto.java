package MyImdb.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailDto {
    private int id;
    private String title;
    private int year;
    private String plot;
    private String director;
    private String writer;
    private String country;
    private String poster;
    private int runtime;
    private float imdbRating;
    private int userRating;
}
