package com.example.shoppingverse.service;

import com.example.shoppingverse.dto.request.LoginRequestDto;
import com.example.shoppingverse.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager       authenticationManager;

    private final JwtUtil jwtUtil;

    public String login(   LoginRequestDto request){

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        //return "Login Successful";
        return jwtUtil.generateToken(  request.getEmail());
    }
}