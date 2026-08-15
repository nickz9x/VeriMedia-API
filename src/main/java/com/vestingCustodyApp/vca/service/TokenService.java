package com.vestingCustodyApp.vca.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vestingCustodyApp.vca.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;


    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("nicholas pereira")
                    .withSubject(user.getLogin())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException jwtCreationException) {
            throw new JWTCreationException("error",jwtCreationException);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("nicholas pereira")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
