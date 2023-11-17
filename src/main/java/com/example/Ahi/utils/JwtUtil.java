package com.example.Ahi.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    public static boolean isExpired(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    //토큰 생성
    public static String createJwt(String member_id, String secretKey, Long expiredMs){
        Claims claims = Jwts.claims();
        claims.put("memberId",member_id);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()*expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //멤버 Id 추출
    public static String extractMember(String token, String secretKey){
        String member_id = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody()
                .get("memberId").toString();

        return member_id;
    }
}
