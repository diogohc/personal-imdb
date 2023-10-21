package MyImdb.demo.scheduletasks;

import MyImdb.demo.utils.DataBaseTasks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.*;

@Slf4j
@RequiredArgsConstructor
public class UpdateMoviesRating {

    private final Environment environment;


    //update movie rating with the ratigns added in the last 24hours
    @Scheduled(cron = "0 0 20 ? * *")
    public void updateMoviesRating(){
        log.info("Process that updates movies rating started");
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = -1;
        StringBuilder SQL = new StringBuilder("");
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbUsername = environment.getProperty("spring.datasource.username");
        String dbPassword = environment.getProperty("spring.datasource.password");
        try{
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            SQL.append(" UPDATE movies AS m JOIN ( SELECT movie_id, AVG(rating) AS AVG_RATING FROM reviews ");
            SQL.append(" WHERE date_added BETWEEN NOW() - INTERVAL 1 DAY AND NOW() GROUP BY movie_id) AS r_avg");
            SQL.append(" ON m.movie_id = r_avg.movie_id SET m.rating = (m.rating + r_avg.AVG_RATING) / 2;");

            pstmt = conn.prepareStatement(SQL.toString());

            result = pstmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataBaseTasks.close(pstmt, conn);
        }

        if(result > 0){
            log.info("Process that updates movies rating ended successfully");
        } else {
            log.info("Process that updates movies rating ended with error");
        }


    }

}
