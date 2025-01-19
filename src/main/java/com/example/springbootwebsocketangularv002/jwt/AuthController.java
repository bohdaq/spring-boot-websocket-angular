package com.example.springbootwebsocketangularv002.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;  // A service to handle user validation (authentication)

    // Endpoint for user login
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (isAuthenticated) {
            String token = jwtTokenUtil.generateToken(loginRequest.getUsername());
            return token;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
