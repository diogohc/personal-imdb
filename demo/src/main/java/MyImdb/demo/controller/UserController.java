package MyImdb.demo.controller;


import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.MovieDto;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.dto.UserDetail;
import MyImdb.demo.service.ReviewService;
import MyImdb.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    private final ReviewService reviewService;

    private final JwtService jwtService;


    @GetMapping("/stats/{id}")
    public ResponseEntity<?> getUserStats(@PathVariable("id") int id){
        //userService.getMapYearNrMovies(id);
        return userService.getUserStats(id);
    }

    @GetMapping("")
    @Operation(summary = "Get user details using JWT")
    public ResponseEntity<UserDetail> getUser(@RequestHeader("Authorization") String authorizationHeader){
        Long userId = jwtService.extractUserId(authorizationHeader);

        UserDetail userDetail = userService.getUserById(userId);
        return new ResponseEntity<>(userDetail, HttpStatus.OK);
    }

    @GetMapping("/logoutUser")
    public ResponseEntity<?> logoutUser(HttpServletRequest request){
        //TODO deactivate session, get the jwt and destroy it?
        request.getSession().invalidate();
        log.info("LOGOUT");
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/export")
    public void exportUsersToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "users_" + currentDateTime + ".xlsx";

        String headerValue = "attachment; filename=" + fileName;

        response.setHeader(headerKey, headerValue);

        userService.exportExcel(response);
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<?> listUserReviews(@PathVariable(name = "userId") int userId){
        List<MovieDto> userReviews = reviewService.listUserReviewsByUserId(userId);
        return new ResponseEntity<Object>(userReviews, HttpStatus.OK);
    }
}
