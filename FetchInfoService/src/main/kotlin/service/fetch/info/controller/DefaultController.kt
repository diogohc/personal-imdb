package service.fetch.info.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class DefaultController {

    @GetMapping("/{imdbId}")
    fun getMovieInfo(@PathVariable imdbId: String){

    }
}