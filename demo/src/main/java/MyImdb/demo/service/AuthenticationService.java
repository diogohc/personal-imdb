package MyImdb.demo.service;

import MyImdb.demo.auth.AuthenticationRequest;
import MyImdb.demo.auth.AuthenticationResponse;
import MyImdb.demo.auth.RegisterRequest;
import MyImdb.demo.config.JwtService;
import MyImdb.demo.dto.ReviewDto;
import MyImdb.demo.model.Role;
import MyImdb.demo.model.User;
import MyImdb.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    ReviewService reviewService;



    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), Role.USER);

        //if username already exists
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return AuthenticationResponse.builder().response("Username already exists").build();
        }

        userRepository.save(user);
        log.info("User {} successfully registered", user.getUsername());
        String token = jwtService.generateToken(user, user.getId(), user.getRole());

        return AuthenticationResponse.builder().response("User successfully registered").build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<User> user = userRepository.findByUsername(request.getUsername());

        //if user doesnt' exit
        if(user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())){
            return AuthenticationResponse.builder().response("Wrong credentials").build();
        }

        log.info("User {} successfully logged in", user.get().getUsername());
        //generate token and return it
        String token = jwtService.generateToken(user.get(), user.get().getId(), user.get().getRole());

        return AuthenticationResponse.builder().response("success").token(token).build();
    }


    public HashMap<Integer, Integer>  populateMapMovieIdRating(String username){
        HashMap<Integer, Integer> hmMovieIdRating = new HashMap<>();
        //Get user reviews. If the user rated the movie, include its value
        Vector<ReviewDto> userReviews= reviewService.listUserReviews(username);
        for(ReviewDto reviewDto: userReviews){
            hmMovieIdRating.put((int) reviewDto.getMovieId(), reviewDto.getRating());
        }
        return hmMovieIdRating;
    }
}
