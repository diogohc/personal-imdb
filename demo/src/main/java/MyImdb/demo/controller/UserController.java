package MyImdb.demo.controller;


import MyImdb.demo.config.JwtService;
import MyImdb.demo.response.DefaultResponse;
import MyImdb.demo.service.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final JwtService jwtService;

    //todo testar endpoint. apagar o /id no postman
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authorizationHeader){
        Long id = jwtService.extractUserId(authorizationHeader.substring("Bearer ".length()));

        ObjectNode json = userService.getUserStats(Math.toIntExact(id));
        //userService.getMapYearNrMovies(id);

        if(json == null){
            return new ResponseEntity<>(new DefaultResponse<>("User doesn't exist with id: " + id, "NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
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
