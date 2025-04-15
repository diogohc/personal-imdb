package com.getinfo.fetch_movie_info.repository;

import com.getinfo.fetch_movie_info.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}