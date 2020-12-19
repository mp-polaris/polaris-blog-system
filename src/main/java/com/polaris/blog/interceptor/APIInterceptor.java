package com.polaris.blog.interceptor;

import com.google.gson.Gson;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.utils.Constants;
import com.polaris.blog.utils.CookieUtil;
import com.polaris.blog.utils.RedisUtil;
import com.polaris.blog.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
@Slf4j
public class APIInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //String name = handlerMethod.getMethod().getName();
            //log.info("method name is " + name);

            CheckTooFrequentCommit frequentCommit = handlerMethod.getMethodAnnotation(CheckTooFrequentCommit.class);
            String methodName = handlerMethod.getMethod().getName();
            if (frequentCommit == null) return true;
            //判断是否真的提交太频繁
            //所有提交内容的方法用户必须是登录了的，所以使用token和方法名作为key来记录请求频率
            String tokenKey = CookieUtil.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
            log.info("tokenKey ==>" + tokenKey);
            if(TextUtil.isEmpty(tokenKey)) return true;
            //从redis里获取，判断是否存在
            String hasCommit = (String)redisUtil.get(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey + methodName);
            if(!TextUtil.isEmpty(hasCommit)){
                //如果存在则返回"提交太频繁"
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                ResponseResult failed = ResponseResult.FAILED("提交过于频繁，请稍后重试");
                PrintWriter writer = response.getWriter();
                writer.write(gson.toJson(failed));
                writer.flush();
                return false;
            }
            //如果不存在，说明可以提交，并且记录此次提交，有效期为30秒
            redisUtil.set(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey + methodName,"true",Constants.TimeValue.SECOND_10);
        }
        //放行
        return true;
    }
}
