package com.polaris.blog.controller.user;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户中心
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserAPI {
    @Autowired
    private UserServiceImpl userService;

    /**
     * 初始化管理员账号
     *
     * @param blogUser
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody BlogUser blogUser) {
        return userService.initManagerAccount(blogUser);
    }

    /**
     * 获取图灵验证码
     *
     * @param captchaKey
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(@RequestParam("captcha_key") String captchaKey) {
        userService.createCaptcha(captchaKey);
    }

    /**
     * 发送验证码邮件:
     * 三种场景：根据type参数决定
     * ① 注册邮箱：如果邮箱已注册，提示该邮箱已经注册
     * ② 修改邮箱：如果邮箱已注册，提示该邮箱已经注册
     * ③ 找回密码：如果邮箱没有注册过，提示该邮箱没有注册
     *
     * @param type
     * @param email
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("type") String type,
                                         @RequestParam String email) {
        return userService.sendEmail(type, email);
    }

    /**
     * 注册
     *
     * @param blogUser
     * @return
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody BlogUser blogUser,
                                   @RequestParam("email_code") String emailCode,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey) {
        return userService.register(blogUser, emailCode, captchaCode, captchaKey);
    }

    /**
     * 登录
     *
     * @param captchaKey
     * @param captcha
     * @param blogUser
     * @return
     */
    @PostMapping("/login/{captcha_key}/{captcha}")
    public ResponseResult login(@PathVariable("captcha_key") String captchaKey,
                                @PathVariable("captcha") String captcha,
                                @RequestBody BlogUser blogUser,
                                @RequestParam(value = "from", required = false) String from) {
        return userService.doLogin(captchaKey, captcha, blogUser, from);
    }

    /**
     * 获取用户信息user-info
     *
     * @param userId
     * @return
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息user-info
     *
     * @param userId
     * @param blogUser
     * @return
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,
                                         @RequestBody BlogUser blogUser) {
        return userService.updateUserInfo(userId, blogUser);
    }

    /**
     * 检查邮箱是否已注册
     *
     * @param email
     * @return
     */
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    /**
     * 检查用户名是否已注册
     *
     * @param userName
     * @return
     */
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName) {
        return userService.checkUserName(userName);
    }

    /**
     * 删除用户（需要管理员权限）
     *
     * @param userId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {

        return userService.deleteUserById(userId);
    }

    /**
     * 获取用户列表（需要管理员权限）
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult getUserList(@RequestParam("page") int page,
                                      @RequestParam("size") int size,
                                      @RequestParam(value = "userName", required = false) String userName,
                                      @RequestParam(value = "email", required = false) String email) {
        return userService.getUserList(page, size, userName, email);
    }

    /**
     * 修改密码（包括修改密码和找回密码）
     * 修改密码：通过旧密码对比来更新密码，需要登录
     * 找回密码：发送验证码到邮箱，判断验证码正确与否来确定该
     * 账号是否属于当前用户，不登陆也能修改。
     * 步骤：用户填写邮箱
     * 用户获取忘记密码的验证码（type=forget）
     * 用户填写新密码
     * 总结：需要提交数据（邮箱和新密码，验证码）
     *
     * @param verifyCode
     * @param blogUser
     * @return
     */
    @PutMapping("/password/{verify_code}")
    public ResponseResult updatePassword(@PathVariable("verify_code") String verifyCode,
                                         @RequestBody BlogUser blogUser) {
        return userService.updateUserPassword(verifyCode, blogUser);
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/reset_password/{userId}")
    public ResponseResult resetPassword(@PathVariable("userId") String userId, @RequestParam("password") String password) {
        return userService.resetUserPassword(userId, password);
    }

    /**
     * 修改邮箱
     * 条件：①用户必须登录 ②修改的邮箱必须是没有注册过的
     * 步骤：
     * 输入新的邮箱
     * 获取验证码
     * 输入验证码
     * 总结：需要提交数据（新的邮箱，验证码，其他信息可以从token中获取）
     *
     * @param verifyCode
     * @param email
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("verify_code") String verifyCode,
                                      @RequestParam("email") String email) {
        return userService.updateUserEmail(verifyCode, email);
    }

    /**
     * 退出登录
     * 步骤：
     * 拿到tokenKey
     * 删除Redis里对应的Token
     * 删除mysql里对应的refreshToken
     * 删除cookie里的tokenKey
     *
     * @return
     */
    @GetMapping("/logout")
    public ResponseResult logout() {
        return userService.doLogout();
    }

    /**
     * 获取二维码：
     * 二维码的图片路径
     * 二维码的内容字符
     *
     * @return
     */
    //@CheckTooFrequentCommit，不行（未登录不能监测）
    //TODO:要防止频繁请求二维码，怎么实现？
    @GetMapping("/pc-login-qr-code")
    public ResponseResult getPcLoginQrCode() {
        return userService.getPcLoginQrCode();
    }

    /**
     * 检查二维码的登录状态
     *
     * @return
     */
    @GetMapping("/qr-code-state/{qrCode}")
    public ResponseResult checkQrCodeLoginState(@PathVariable("qrCode") String qrCode) {
        return userService.checkQrCodeLoginState(qrCode);
    }

    /**
     * 更新二维码的登录状态
     *
     * @return
     */
    @PutMapping("/qr-code-state/{qrCode}")
    public ResponseResult updateQrCodeLoginState(@PathVariable("qrCode") String qrCode) {
        return userService.updateQrCodeLoginState(qrCode);
    }

    @GetMapping("/check-token")
    public ResponseResult parseToken() {
        return userService.parseToken();
    }

    /**
     * 获取用户总数
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/register_count")
    public ResponseResult registerCount() {
        return userService.registerCount();
    }
}
