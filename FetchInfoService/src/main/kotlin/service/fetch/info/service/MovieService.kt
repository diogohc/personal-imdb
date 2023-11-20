package service.fetch.info.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import service.fetch.info.entity.Movie
import service.fetch.info.repository.MovieRepository

@Service
class MovieService @Autowired constructor(
    private val movieRepository: MovieRepository
    ) {

    fun saveMovie(movie: Movie){
        movieRepository.save(movie);
    }
}