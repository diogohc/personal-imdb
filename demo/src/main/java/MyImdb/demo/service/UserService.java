package MyImdb.demo.service;

import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.dto.UserDto;
import MyImdb.demo.model.Review;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.ReviewRepository;
import MyImdb.demo.repository.UserRepository;
import MyImdb.demo.utils.ExcelUser;
import MyImdb.demo.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    private ObjectMapper objectMapper;


    private static Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public Optional<User> findUserById(long id){
        return this.userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public List<User> findAll(){
        return this.userRepository.findAll();
    }

    public ResponseEntity<?> getUserStats(int userId){
        int totalNrMoviesWatched;
        int totalMinutesMoviesWatched;
        logger.info("Getting user stats for user: " + userId);
        Optional<User> user = userRepository.findById(1L);
        if(user.isPresent()){
            totalNrMoviesWatched = reviewRepository.nrMoviesWatched(user.get().getId());
            totalMinutesMoviesWatched = reviewRepository.minutesMoviesWatched(user.get().getId());

            ObjectNode json = objectMapper.createObjectNode();
            json.put("nrMoviesWatched", totalNrMoviesWatched);
            json.put("minutesMoviesWatched", totalMinutesMoviesWatched);
            return new ResponseEntity<Object>(json, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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

    public List<UserDto> getAllUsersWithReviews(){
        List<User> listUsers = userRepository.findAll();
        List<UserDto> listUsersDto = new ArrayList<>();
        List<Review> userReviews = new ArrayList<>();
        List<ReviewDto> userReviewsDto = null;
        for(User user: listUsers){
            userReviewsDto = new ArrayList<>();
            userReviews = reviewRepository.getReviewsByUserId(Math.toIntExact(user.getId()), Sort.by("date_added"));
            for(Review review: userReviews){
                ReviewDto reviewDto = new ReviewDto(review.getId(), review.getMovie().getId(), review.getRating(),
                        review.getMovie().getPoster(), review.getMovie().getTitle(), review.getDate_added());
                userReviewsDto.add(reviewDto);
            }
            UserDto userDto = new UserDto(user.getUsername(), user.getRole(), userReviewsDto);
            listUsersDto.add(userDto);
        }

        return listUsersDto;
    }

    public List<UserDto> getAllUsers(){
        return ObjectMapperUtils.mapAll(userRepository.findAll(), UserDto.class);
    }

}
