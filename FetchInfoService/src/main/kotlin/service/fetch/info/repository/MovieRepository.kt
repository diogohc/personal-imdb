package service.fetch.info.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import service.fetch.info.entity.Movie

@Repository
interface MovieRepository: JpaRepository<Movie, Long> {
}