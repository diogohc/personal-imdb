package service.fetch.info.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchInfoService {
    fun getMovieInfo(imdbId: String){
        var apiKey = "a9c633d3";
        var url : URL;
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