package MyImdb.demo.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseTasks {
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn){
        closeResultSet(rs);
        closePreparedStatement(pstmt);
        closeConnection(conn);
    }

    public static void closeResultSet(ResultSet rs){
        try{
            if(rs != null){
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePreparedStatement(PreparedStatement pstmt){
        try{
            if(pstmt != null){
                pstmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection conn){
        try{
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
