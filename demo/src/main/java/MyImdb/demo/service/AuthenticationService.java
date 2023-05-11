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
        log.info("User "+user.getUsername()+" is registered");
        String token = jwtService.generateToken(user);
        log.info("Registration generated JWT: "+token);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<User> user = userRepository.findByUsername(request.getUsername());

        //if user doesnt' exit
        if(user.isEmpty()){
            return AuthenticationResponse.builder().response("User doesn't exist").build();
        }

        //if password is wrong
        if(!passwordEncoder.matches(request.getPassword(), user.get().getPassword())){
            return AuthenticationResponse.builder().response("Wrong password").build();
        }

        log.info("User "+user.get().getUsername()+" logged in");
        //generate token and return it
        String token = jwtService.generateToken(user.get());
        log.info("Authentication generated JWT: "+token);
        return AuthenticationResponse.builder().token(token).role(user.get().getRole()).id(Math.toIntExact(user.get().getId())).build();
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
