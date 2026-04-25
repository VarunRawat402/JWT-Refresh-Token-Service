package com.example.RefreshTokenPrac.controller;


import com.example.RefreshTokenPrac.entities.RefreshToken;
import com.example.RefreshTokenPrac.entities.UserInfo;
import com.example.RefreshTokenPrac.request.LoginDTO;
import com.example.RefreshTokenPrac.request.RefreshTokenRequestDTO;
import com.example.RefreshTokenPrac.response.JwtResponseDTO;
import com.example.RefreshTokenPrac.service.JwtService;
import com.example.RefreshTokenPrac.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class TokenController
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    //Login to get JWT Token and Refresh Token
    //Used when your JWT and Refresh both are expired
    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody LoginDTO loginDTO){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDTO.getUsername());
            String jwtToken = jwtService.GenerateToken(loginDTO.getUsername());

            return new ResponseEntity<>(JwtResponseDTO.builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //To get JWT Token using Refresh Token
    //Used when your jwt is expired but refresh is not expired
    @PostMapping("/auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshTokenRequestDTO.getToken());
        if (refreshTokenOpt.isEmpty()) {
            throw new RuntimeException("Refresh Token is not in DB..!!");
        }

        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
        UserInfo userInfo = verifiedToken.getUserInfo();
        if (userInfo == null) {
            throw new RuntimeException("User not found for this refresh token..!!");
        }

        String accessToken = jwtService.GenerateToken(userInfo.getUsername());
        return JwtResponseDTO.builder()
                .accessToken(accessToken)
                .token(refreshTokenRequestDTO.getToken())
                .build();
    }
}
