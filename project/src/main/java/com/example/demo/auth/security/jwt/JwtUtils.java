package com.example.demo.auth.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.auth.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  private String jwtSecret;
  private Algorithm algorithm;
  private JWTVerifier verifier;

  public JwtUtils(@Value("${security.jwt.secret}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
    algorithm = Algorithm.HMAC256(jwtSecret);
    verifier = JWT.require(algorithm).build();
  }

  public String generateJwtToken(String username) {

    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);
    calendar.add(Calendar.HOUR_OF_DAY, 2);
    Date expiresAt = calendar.getTime();
    return JWT.create()
            .withSubject(username)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
  }

  public String getUsername(String jwtToken) {
    try {
      DecodedJWT decodedJWT = verifier.verify(jwtToken);
      return decodedJWT.getSubject();
    } catch (JWTVerificationException e) {
      logger.error("getUsername failed because of invalid jwt. ", e);
      return null;
    }
  }

  public boolean validateJwtToken(String token) {
    try {
      verifier.verify(token);
      return true;
    } catch (JWTVerificationException e) {
      logger.error("validateJwtToken failed", e);
      return false;
    }
  }
}
