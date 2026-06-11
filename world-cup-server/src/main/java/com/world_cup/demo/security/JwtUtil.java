//package com.world_cup.demo.security;
//
//import org.springframework.stereotype.Component;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//
//import java.security.Key;
//import java.util.Date;
//import java.security.Key;
//
//@Component
//public class JwtUtil {
//    private final String SECRET = "mysecretmysecretmysecretmysecretmysecretmysecretmysecret";
//    private final long EXPIRATION = 1000*60*60*24;
//
//    private Key getSignKey(){
//        return Keys.hmacShaKeyFor(SECRET.getBytes());
//    }
//    public String generateToken(Long userId){
//        return Jwts.builder()
//                .setSubject(String.valueOf(userId)).setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public Long extractUserId(String token) {
//        return Long.parseLong(
//                Jwts.parserBuilder()
//                        .setSigningKey(getSignKey())
//                        .build()
//                        .parseClaimsJws(token)
//                        .getBody()
//                        .getSubject()
//        );
//    }
//
//    public boolean isValid(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSignKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//}
