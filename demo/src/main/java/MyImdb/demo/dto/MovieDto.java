package MyImdb.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    int movieId;
    String movieTitle;
    String moviePoster;
    int reviewRating;
}
