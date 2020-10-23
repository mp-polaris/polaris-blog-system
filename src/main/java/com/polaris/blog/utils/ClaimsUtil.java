package com.polaris.blog.utils;

import com.polaris.blog.pojo.BlogUser;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtil {
    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String EMAIL = "email";
    public static final String ROLES = "roles";
    public static final String AVATAR = "avatar";
    public static final String SIGN = "sign";
    public static final String FROM = "from";

    public static Map<String,Object> blogUserToClaims(BlogUser blogUser,String from){
        Map<String,Object> claims = new HashMap<>();
        claims.put(ID,blogUser.getId());
        claims.put(USER_NAME,blogUser.getUserName());
        claims.put(EMAIL,blogUser.getEmail());
        claims.put(ROLES,blogUser.getRoles());
        claims.put(AVATAR,blogUser.getAvatar());
        claims.put(SIGN,blogUser.getSign());
        claims.put(FROM,from);
        return  claims;
    }

    public static BlogUser claimsToBlogUser(Map<String,Object> claims){
        BlogUser blogUser = new BlogUser();
        blogUser.setId((String)claims.get(ID));
        blogUser.setUserName((String)claims.get(USER_NAME));
        blogUser.setEmail((String)claims.get(EMAIL));
        blogUser.setRoles((String)claims.get(ROLES));
        blogUser.setAvatar((String)claims.get(AVATAR));
        blogUser.setSign((String)claims.get(SIGN));
        return blogUser;
    }

    public static String getFrom(Claims claims) {
        return (String) claims.get(FROM);
    }
}
