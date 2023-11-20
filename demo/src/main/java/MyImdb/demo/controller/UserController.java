package MyImdb.demo.controller;


import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.UserDetail;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    private final JwtService jwtService;



    @Operation(summary = "Get user stats")
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("userId") int userId){

        Long id = jwtService.extractUserId(authorizationHeader);

        if(id == -1){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        log.info("[GET] - Get stats for user with id {} by user with id {}", userId, id);
        ObjectNode json = userService.getUserStats(userId);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(summary = "Get user details using JWT")
    public ResponseEntity<UserDetail> getUserDetails(@RequestHeader("Authorization") String authorizationHeader){
        Long userId = jwtService.extractUserId(authorizationHeader);
        log.info("[GET] - Get user details for user with id {}", userId);
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

    @PostMapping("/import")
    @Operation(summary = "Import user's imdb ratings csv file to the application. Insert the movie in the DB if don't exist" +
            "and creates the rating object for the movie, based on the user")
    public ResponseEntity<?> importImdbInfo(@RequestHeader("Authorization") String authorizationHeader){
        //TODO get the file from the request
        File f = new File("");
        Long userId = jwtService.extractUserId(authorizationHeader);

        //userService.importUserRatingsInfo(f, userId);

        return null;
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


}
