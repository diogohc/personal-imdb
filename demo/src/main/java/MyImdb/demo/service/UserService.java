package MyImdb.demo.service;

import MyImdb.demo.entity.User;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import MyImdb.demo.utils.DataBaseTasks;
import MyImdb.demo.utils.ExcelUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
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

    //private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public Optional<User> findUserById(long id){
        return this.userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public List<User> findAll(){
        return this.userRepository.findAll();
    }

    public ObjectNode getUserStats(int userId){
        int totalNrMoviesWatched;
        int totalMinutesMoviesWatched;
        ObjectNode json = null;
        log.info("Getting user stats for user: " + userId);
        Optional<User> user = userRepository.findById((long) userId);
        if(user.isPresent()){
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

            return json;
        }
        return null;
    }

    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userRepository.findAll();
        ExcelUser excelExporter = new ExcelUser(listUsers);
        excelExporter.export(response);
    }

    public Long getUserIdWithUsername(String username){
        Optional<User> user =  userRepository.findByUsername(username);

        if(user.isPresent()){
            return user.get().getId();
        }else {
            return (long) -1;
        }
    }


    public Map<Integer, Integer> getMapYearNrMovies(int userId){
        Map<Integer, Integer> mapYearNrMovies = new HashMap<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer SQL = new StringBuffer("");
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbUsername = environment.getProperty("spring.datasource.username");
        String dbPassword = environment.getProperty("spring.datasource.password");
        try{
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
            DataBaseTasks.close(rs, pstmt, conn);
        }
        return mapYearNrMovies;
    }

}
