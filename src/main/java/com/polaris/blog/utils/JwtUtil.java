package com.polaris.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.DigestUtils;

import java.util.Calendar;
import java.util.Map;

public class JwtUtil {
    //私钥
    private static String tokenSecret = DigestUtils.md5DigestAsHex("polaris_blog_system_mg".getBytes());
    //过期时间，单位是秒
    private  static int expirationTimeInSecond = Constants.TimeValue.HOUR_2;

    /**
     * 获取Token
     */
    public static String getToken(Map<String, Object> claims, int expirationTimeInSecond) {
        JwtUtil.expirationTimeInSecond = expirationTimeInSecond;
        return getToken(claims);
    }

    /**
     * 获取Token
     */
    public static String getToken(Map<String, Object> claims) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,JwtUtil.expirationTimeInSecond);
        JwtBuilder builder = Jwts.builder();
        if (claims != null) {
            builder.setClaims(claims);
        }
        //注意：先设置过期时间会被claims覆盖
        builder.setExpiration(calendar.getTime()).signWith(SignatureAlgorithm.HS256, tokenSecret);
        return builder.compact();
    }

    /**
     * 获取refreshToken，简单起见载荷只设置一个userId即可
     * refreshToken作用：
     *      我们可以通过refreshToken(长时间有效比如一个月，存在MySQL数
     *      据库里)来生成新的Token(短时间有效比如2小时，存在Redis中)
     */
    public static String getRefreshToken(String userId, int expirationTimeInSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,expirationTimeInSecond);

        JwtBuilder builder = Jwts.builder()
                .setId(userId)
                .signWith(SignatureAlgorithm.HS256, tokenSecret);
        if (expirationTimeInSecond > 0) {
            builder.setExpiration(calendar.getTime());
        }
        return builder.compact();
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String jwtStr) {
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(jwtStr)
                .getBody();
    }

}