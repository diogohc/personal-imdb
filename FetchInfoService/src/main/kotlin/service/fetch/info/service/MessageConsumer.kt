package service.fetch.info.service

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Service
class MessageConsumer(private val movieService: MovieService) {

    @Value("\${rabbitmq.queue.name}")
    private lateinit var queueName: String

    @RabbitListener(queues = ["myQueue"])
    fun receiveMessage(message: String){
        println("MESSAGE: $message")
        //get movie info
        val response = makeApiRequest(message)

        //save movie
        println("RESPONSE: $response")
    }


    fun makeApiRequest(imdbId: String){
        //TODO add key in properties file
        var apiKey = ""
        var url : URL
        var requestUrl = StringBuilder()
        val baseUrl: String = "https://www.omdbapi.com/?i="
        requestUrl.append(baseUrl).append(imdbId).append("&apikey=").append(apiKey)


        url = URL(requestUrl.toString())
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if(responseCode == HttpURLConnection.HTTP_OK){
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.use { it.readText() }
            println(response)
        } else {
            println("Error: $responseCode")
        }
    }
}