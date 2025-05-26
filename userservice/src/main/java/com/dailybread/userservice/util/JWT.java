package com.dailybread.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

public class JWT {
    
    private static final String SECRET_KEY = "SLDFKJSDFklsdfjskjdrtrtfdhfghrturetyfyrtyrthrtyrty";
    private static final long EXPIRATION_TIME = 86400000;
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String email){
        return Jwts.builder().subject(email).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(key).compact();
    }

    public static String validateToken(String token){
        try{
            // return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody().getSubject();
            Jws<Claims> parsed = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            // return claims.getSubject();
            System.out.println("verfied "+parsed.getPayload().getSubject());
            return parsed.getPayload().getSubject();
        }catch(JwtException er){
            System.out.println("invalid token");
            return "invalid token";
        }catch(Exception e){
            System.out.println("unknown error");
            e.printStackTrace();
            return null;
        }
    }

}
