package MyImdb.demo.service;

import MyImdb.demo.dto.UserDetail;
import MyImdb.demo.enums.AddExternalMovieStatus;
import MyImdb.demo.exceptions.ResourceNotFoundException;
import MyImdb.demo.model.Movie;
import MyImdb.demo.model.Review;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import MyImdb.demo.utils.DataBaseUtils;
import MyImdb.demo.utils.ExcelUser;
import MyImdb.demo.utils.MovieUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    private final Environment environment;

    private final MovieService movieService;


    public ObjectNode getUserStats(int userId){
        int totalNrMoviesWatched;
        int totalMinutesMoviesWatched;
        ObjectNode json = null;

        Optional<User> user = userRepository.findById((long) userId);
        if(user.isPresent()){
            log.info("Getting user stats for user: " + userId);

            totalNrMoviesWatched = reviewRepository.nrMoviesWatched(user.get().getId());
            totalMinutesMoviesWatched = reviewRepository.minutesMoviesWatched(user.get().getId());

            Map<Integer, Integer> mapYearNrMoviesWatched = getMapYearNrMovies(userId);

            //Create "main" JSON with all stats
            json = objectMapper.createObjectNode();
            json.put("nrMoviesWatched", totalNrMoviesWatched);
            json.put("minutesMoviesWatched", totalMinutesMoviesWatched);

            //create JSON with stats by year
            ArrayNode nrMoviesPerYearJson = objectMapper.createArrayNode();

            for(int key: mapYearNrMoviesWatched.keySet()){
                ObjectNode jsonYearCount = objectMapper.createObjectNode();
                jsonYearCount.put("year", key);
                jsonYearCount.put("count", mapYearNrMoviesWatched.get(key));
                nrMoviesPerYearJson.add(jsonYearCount);
            }

            //set the yearly stats to the main json
            json.set("nrMoviesPerYear", nrMoviesPerYearJson);
        }
        return json;
    }

    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userRepository.findAll();
        ExcelUser excelExporter = new ExcelUser(listUsers);
        excelExporter.export(response);
    }

    public int getUserIdWithUsername(String username){
        Optional<User> user =  userRepository.findByUsername(username);
        return user.map(value -> value.getId().intValue()).orElse(-1);
    }


    public Map<Integer, Integer> getMapYearNrMovies(int userId){
        Map<Integer, Integer> mapYearNrMovies = new HashMap<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder SQL = new StringBuilder("");
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbUsername = environment.getProperty("spring.datasource.username");
        String dbPassword = environment.getProperty("spring.datasource.password");
        try{
            assert dbUrl != null;
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            SQL.append("SELECT YEAR(date_added) as YEAR, count(*) as NRFILMS FROM reviews ");
            SQL.append(" WHERE user_id = ? group by YEAR(date_added);");

            pstmt = conn.prepareStatement(SQL.toString());
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while(rs.next()){
                mapYearNrMovies.put(rs.getInt("YEAR"), rs.getInt("NRFILMS"));
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataBaseUtils.close(rs, pstmt, conn);
        }
        return mapYearNrMovies;
    }


    public UserDetail getUserById(long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User doesn't exist with given id: " + userId));

        return new UserDetail(Math.toIntExact(user.getId()), user.getUsername(), user.getRole());
    }

    //bulk import through CSV file
    //TODO: change logic. Send imdb IDs through rabbitmq instead of inserting here
    public void importUserRatingsInfo(File f, Long userId){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        List<String[]> lstMovieRatingInfo = MovieUtils.readImdbRatingsCsvFile(f);
        AddExternalMovieStatus insertMovieStatus;
        int nrMoviesAdded = 0;
        int nrReviewsAdded = 0;
        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent()){
            for(String[] movieRatingInfo: lstMovieRatingInfo){
/*
                try {
                    //get movie info and insert in the db if it doesn't exist
                    //insertMovieStatus = movieService.addMovie(movieRatingInfo[0]);
                    date = (Date) dateFormat.parse(movieRatingInfo[2]);

                    if(insertMovieStatus == AddExternalMovieStatus.MOVIE_SAVED_SUCCESSFULLY){
                        nrMoviesAdded++;
                    }
                    //create a review for the movie
                    if(insertMovieStatus == AddExternalMovieStatus.MOVIE_ALREADY_EXISTS_IN_DB || insertMovieStatus == AddExternalMovieStatus.MOVIE_SAVED_SUCCESSFULLY){
                        Movie movie = movieService.getMovieByImdbID(movieRatingInfo[0]);
                        Review rev = new Review(user.get(), movie, Integer.parseInt(movieRatingInfo[1]), new Timestamp(date.getTime()));
                        reviewRepository.save(rev);
                        nrReviewsAdded++;
                    }
                } catch (JSONException | JsonProcessingException | ParseException e) {
                    log.error("Error importing user ratings info");
                    throw new RuntimeException(e);
                }

 */
            }
            log.info("User {} added {} movies", userId, nrMoviesAdded);
            log.info("User {} added {} reviews", userId, nrReviewsAdded);
        }
    }

}
