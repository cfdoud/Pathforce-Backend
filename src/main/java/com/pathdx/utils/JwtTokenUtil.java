package com.pathdx.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathdx.security.LoggedInUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 30 * 24 * 60 * 60;
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private Clock clock;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(LoggedInUserDetail userDetails) {
        String userJsonString = null;
        try {
            userJsonString = new ObjectMapper().writeValueAsString(userDetails);
        } catch (JsonProcessingException e) {
            //throw new JsonParsingException("INVALID_JSON");
        }
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + JWT_TOKEN_VALIDITY * 1000);
        log.debug("JWTExpiry " + expiryDate);
        Map<String, Object> userClaim = new HashMap<>();
        userClaim.put("user", userJsonString);
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).addClaims(userClaim)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
//                .setAudience(userDetails.getUserType())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

   public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }

      public String retrieveUserNameFromToken(Map<String, String> headers) {
        String token = headers.get("authorization");
        String jwt = token.replaceAll("Bearer ", "");
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0].trim()));
        String payload = new String(decoder.decode(chunks[1]));
        String[] slicedPayload = payload.split(",");
        String email = null;
        for (String username: slicedPayload) {
            String[] emailId = username.split(":");
            if(emailId[0].equals("\"sub\"")){
                email = emailId[1].substring(1,emailId[1].length()-1);
                return email;
            }
        }
        return null;
    }
    public String retrievePasswordFromToken(Map<String, String> headers) {
        String token = headers.get("authorization");

        String jwt = token.replaceAll("Bearer ", "");
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0].trim()));
        String payload = new String(decoder.decode(chunks[1]));
        String[] slicedPayload = payload.split(",");

        String password = null;
        String emailId = null;
        for (String slice: slicedPayload) {
            String[] sliced = slice.split(":");
            if(sliced[0].equals("\"sub\"")){
                emailId = sliced[1].substring(1,sliced[1].length()-1);
                for(String psw: slicedPayload){
                    String[] sliceStrings = psw.split(":");
                    if(sliceStrings[0].substring(1,sliceStrings[0].length() - 1 ).equals(emailId)){
                        password = sliceStrings[1].substring(1,sliceStrings[1].length()-1);
                        return password;
                    }
                }
            }
        }
        return null;
    }
    public String get(String name, String value) throws UnsupportedEncodingException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(name, value);
        claims.put("random", String.valueOf(UUID.randomUUID()));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(name)
                .setIssuedAt(clock.now())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(30).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

    }
}
