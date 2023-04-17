package MyImdb.demo.controller;

import MyImdb.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/backoffice")
public class BackofficeController {
    private static final Logger logger = LoggerFactory.getLogger(BackofficeController.class.getName());

    @Autowired
    UserService userService;

    @GetMapping("/usersReviews")
    public ResponseEntity<?> getAllUsersWithReviews(){
        logger.info("[GET] - Get all users with reviews");

        return new ResponseEntity<Object>(userService.getAllUsersWithReviews(), HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        logger.info("[GET] - Get all users");
        return new ResponseEntity<Object>(userService.getAllUsers(), HttpStatus.OK);
    }

}
