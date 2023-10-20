package MyImdb.demo.controller;

import MyImdb.demo.auth.AuthenticationRequest;
import MyImdb.demo.auth.AuthenticationResponse;
import MyImdb.demo.auth.RegisterRequest;
import MyImdb.demo.service.AuthenticationService;

import MyImdb.demo.utils.UserSessionData;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

@CrossOrigin(origins = "*", allowedHeaders = "*")

public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @Operation(summary = "Authenticate registered user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
