package com.polaris.blog.services.impl;

import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.services.UserService;
import com.polaris.blog.utils.Constants;
import com.polaris.blog.utils.CookieUtil;
import com.polaris.blog.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("permission")
public class PermissionService {
    @Autowired
    private UserService userService;

    /**
     * 判断是否登录，是否是管理员
     * @return
     */
    public boolean admin(){
        //1.拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        //没有令牌的key，没有登录
        if (TextUtil.isEmpty(tokenKey)) return false;
        BlogUser blogUser = userService.checkBlogUser();
        if (blogUser == null) return false;
        if (Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //是管理员
            return true;
        }
        return false;
    }
}
