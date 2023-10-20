package MyImdb.demo.service;

import MyImdb.demo.auth.AuthenticationRequest;
import MyImdb.demo.auth.AuthenticationResponse;
import MyImdb.demo.auth.RegisterRequest;
import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.entity.Role;
import MyImdb.demo.entity.User;
import MyImdb.demo.repository.UserRepository;
import MyImdb.demo.utils.UserData;
import MyImdb.demo.utils.UserSessionData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    ReviewService reviewService;



    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), Role.USER);

        //if username already exists
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return AuthenticationResponse.builder().response("Username already exists").build();
        }

        userRepository.save(user);
        logger.info("User "+user.getUsername()+" is registered");
        String token = jwtService.generateToken(user, user.getId(), user.getRole());
        logger.info("Registration generated JWT: "+token);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<User> user = userRepository.findByUsername(request.getUsername());

        //if user doesnt' exit
        if(user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())){
            return AuthenticationResponse.builder().response("Wrong credentials").build();
        }


        //Populate User Data
        UserData userData = new UserData();
        userData.mapMovieIdRating = populateMapMovieIdRating(user.get().getId());

        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setUserData(userData);

        logger.info("User \""+user.get().getUsername()+"\" logged in");
        //generate token and return it
        String token = jwtService.generateToken(user.get(), user.get().getId(), user.get().getRole());
        logger.info("Authentication generated JWT: "+token);

        return AuthenticationResponse.builder().token(token).id(Math.toIntExact(user.get().getId())).role(user.get().getRole()).build();
    }


    public HashMap<Integer, Integer>  populateMapMovieIdRating(Long userId){
        HashMap<Integer, Integer> hmMovieIdRating = new HashMap<>();
        //Get user reviews
        Vector<ReviewDto> userReviews= reviewService.listUserReviews(userId);
        for(ReviewDto reviewDto: userReviews){
            hmMovieIdRating.put((int) reviewDto.getMovieId(), reviewDto.getRating());
        }
        return hmMovieIdRating;
    }
}
