package service.fetch.info.service

import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import service.fetch.info.dto.MovieDTO
import service.fetch.info.entity.Movie
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Timestamp


@Service
class MessageConsumer(private val movieService: MovieService) {

    @Value("\${API_KEY}")
    private lateinit var apiKey : String

    @RabbitListener(queues = ["\${rabbitmq.queue.name}"])
    fun receiveMessage(message: String){
        //TODO change to log
        println("Message received from the rabbitmq: $message")
        //get movie info
        val movieDto = makeApiRequest(message)

        //save movie
        if (movieDto != null) {
            val m = Movie(movieDto.Title, movieDto.Year.toInt(), movieDto.Plot, movieDto.Director, movieDto.Writer, movieDto.Country,
            movieDto.Poster, movieDto.imdbID, movieDto.Runtime.split(" ")[0].toInt(), movieDto.imdbRating.toFloat(), movieDto.Genre,
                Timestamp(System.currentTimeMillis())
            )

            //TODO change to log
            println("Adding the movie with the imdb_id $message to the database");
            movieService.saveMovie(m);
        }
    }

    fun makeApiRequest(imdbId: String): MovieDTO? {
        val url : URL
        val requestUrl = StringBuilder()
        val baseUrl: String = "https://www.omdbapi.com/?i="
        var movieJson: MovieDTO? = null
        requestUrl.append(baseUrl).append(imdbId).append("&apikey=").append(apiKey)


        url = URL(requestUrl.toString())
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if(responseCode == HttpURLConnection.HTTP_OK){
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.use { it.readText() }

            movieJson = Gson().fromJson(response, MovieDTO::class.java)

            //TODO change to log
            if(movieJson.Response == "False"){
                println("Incorrect IMDB id")
                return null
            }
            if(movieJson.Type != "movie") {
                println("Incorrect type. Must be movie")
                return null
            }

        } else {
            println("Error: $responseCode")
        }

        return movieJson
    }
}