package MyImdb.demo.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="reviews")
public class Review {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JsonBackReference // Use @JsonBackReference to handle the circular reference
    private User user;
    @ManyToOne
    @JsonManagedReference
    private Movie movie;
    int rating;
    private Timestamp date_added;

    public Review(User user, Movie movie, int rating, Timestamp timestamp) {
        this.user = user;
        this.movie = movie;
        this.rating = rating;
        this.date_added = timestamp;
    }
}
