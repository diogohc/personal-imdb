package MyImdb.demo.controller;

import MyImdb.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/backoffice")
@RequiredArgsConstructor
public class BackofficeController {

    private final UserService userService;

    @GetMapping("/usersReviews")
    public ResponseEntity<?> getAllUsersWithReviews(){
        log.info("[GET] - Get all users with reviews");

        return new ResponseEntity<Object>(userService.getAllUsersWithReviews(), HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        log.info("[GET] - Get all users");
        return new ResponseEntity<Object>(userService.getAllUsers(), HttpStatus.OK);
    }

}
