package MyImdb.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDto {
    private long id;
    private long movieId;
    private int rating;
    private String poster;
    private String movieTitle;
    private Timestamp dateAdded;
}
