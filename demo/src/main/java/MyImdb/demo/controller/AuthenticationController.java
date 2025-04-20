package MyImdb.demo.controller;

import MyImdb.demo.auth.AuthenticationRequest;
import MyImdb.demo.auth.AuthenticationResponse;
import MyImdb.demo.auth.RegisterRequest;
import MyImdb.demo.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request);


        if("Username already exists".equals(response.getResponse())){
            return ResponseEntity.status(HttpURLConnection.HTTP_OK).body(response);
        }

        return ResponseEntity.status(HttpURLConnection.HTTP_CREATED).body(response);
    }
    @Operation(summary = "Authenticate registered user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        if(authResponse.getToken() == null){
            return ResponseEntity.status(HttpURLConnection.HTTP_UNAUTHORIZED).body(authResponse);
        }

        return ResponseEntity.ok(authResponse);
    }

}
