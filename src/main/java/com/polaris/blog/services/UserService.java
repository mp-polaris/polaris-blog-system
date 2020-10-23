package com.polaris.blog.services;

import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.response.ResponseResult;

public interface UserService {
    ResponseResult initManagerAccount(BlogUser blogUser);

    void createCaptcha(String captchaKey);

    ResponseResult sendEmail(String type,String emailAddress);

    ResponseResult register(BlogUser blogUser,String emailCode,String captchaCode,String captchaKey);

    ResponseResult doLogin(String captchaKey, String captcha, BlogUser blogUser,String from);

    BlogUser checkBlogUser();

    ResponseResult getUserInfo(String userId);


    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, BlogUser blogUser);

    ResponseResult deleteUserById(String userId);

    ResponseResult getUserList(int page, int size);

    ResponseResult updateUserPassword(String verifyCode, BlogUser blogUser);

    ResponseResult updateUserEmail(String verifyCode, String email);

    ResponseResult doLogout();

    ResponseResult getPcLoginQrCode();

    ResponseResult checkQrCodeLoginState(String qrCode);

    ResponseResult updateQrCodeLoginState(String qrCode);

    ResponseResult parseToken();
}
