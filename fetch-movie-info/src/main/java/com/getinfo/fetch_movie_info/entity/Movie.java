package com.getinfo.fetch_movie_info.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String title;
    public int year;
    public String plot;
    public String director;
    public String writer;
    public String country;
    public String poster;
    @Column(unique=true)
    public String imdbId;
    public int runtime; 
    public float imdbRating;
    public String genre;
    public Timestamp dt_created;

    public Movie(String title, int year, String plot, String director, String writer, String country, String poster, String imdbId, int runtime, float imdbRating, String genre, Timestamp dt_created) {
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
