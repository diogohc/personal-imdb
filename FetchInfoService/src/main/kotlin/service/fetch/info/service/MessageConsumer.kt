package service.fetch.info.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import service.fetch.info.dto.MovieDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


@Service
class MessageConsumer(private val movieService: MovieService) {

    @Value("\${rabbitmq.queue.name}")
    private lateinit var queueName: String

    @Value("\${API_KEY}")
    private lateinit var apiKey : String

    @RabbitListener(queues = ["myQueue"])
    fun receiveMessage(message: String){
        println("MESSAGE: $message")
        //get movie info
        val response = makeApiRequest(message)

        //save movie
        println("RESPONSE: ${response.toString()}")

        if (response != null) {
            println(response.Title)
        }
    }


    fun makeApiRequest(imdbId: String): MovieDTO? {

        var url : URL
        var requestUrl = StringBuilder()
        val baseUrl: String = "https://www.omdbapi.com/?i="
        val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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
            if(movieJson.Type != "movie"){
                println("Incorrect type. Must be movie")
                return null
            }

        } else {
            println("Error: $responseCode")
        }

        return movieJson
    }
}