package com.example.springbootwebsocketangularv002.jwt;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
