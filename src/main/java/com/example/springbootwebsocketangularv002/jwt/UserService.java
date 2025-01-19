package com.example.springbootwebsocketangularv002.jwt;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public boolean authenticate(String username, String password) {
        // for demo purposes bypass authentication
        return true;
    }
}