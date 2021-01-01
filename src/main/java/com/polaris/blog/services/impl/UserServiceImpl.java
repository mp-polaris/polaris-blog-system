package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.BlogUserMapper;
import com.polaris.blog.dao.RefreshTokenMapper;
import com.polaris.blog.dao.SettingMapper;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.RefreshToken;
import com.polaris.blog.pojo.Setting;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.response.ResponseState;
import com.polaris.blog.services.UserService;

import com.polaris.blog.utils.*;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends BaseService implements UserService {
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Random random;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CountDownLatchManager countDownLatchManager;

    private static final int[] CAPTCHA_FONT_TYPES = {
            Captcha.FONT_1, Captcha.FONT_2, Captcha.FONT_3, Captcha.FONT_4, Captcha.FONT_5,
            Captcha.FONT_6, Captcha.FONT_7, Captcha.FONT_8, Captcha.FONT_9, Captcha.FONT_10};

    @Override
    public ResponseResult initManagerAccount(BlogUser blogUser) {
        //检查是否有初始化
        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        Setting managerAccountState = settingMapper.selectByKey(Constants.Setting.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化过了");
        }
        //检查数据
        if (TextUtil.isEmpty(blogUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtil.isEmpty(blogUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtil.isEmpty(blogUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        //补充数据
        blogUser.setId(snowflakeIdWorker.nextId() + "");
        blogUser.setRoles(Constants.User.ROLE_ADMIN);
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setState(Constants.User.DEFAULT_STATE);
        blogUser.setLoginIp(this.getRequest().getRemoteAddr());
        blogUser.setRegIp(this.getRequest().getRemoteAddr());
        blogUser.setCreateTime(new Date());
        blogUser.setUpdateTime(new Date());
        //使用SpringSecurity的BCryptPasswordEncoder对密码进行加密
        String encode = passwordEncoder.encode(blogUser.getPassword());
        blogUser.setPassword(encode);
        //保存到数据库
        MapperUtil.getMapper(BlogUserMapper.class).insert(blogUser);
        //更新已经添加的标记
        Setting setting = new Setting();
        setting.setId(snowflakeIdWorker.nextId() + "");
        setting.setKey(Constants.Setting.MANAGER_ACCOUNT_INIT_STATE);
        setting.setValue("1");
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        settingMapper.insert(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }

    /**
     * EasyCaptcha：https://github.com/whvcse/EasyCaptcha
     * captchaKey:时间戳
     */
    @Override
    public void createCaptcha() {
        try {
            //生成captchaKey，为了防止重复创建占用redis资源，先检查Cookie中是否有上一次的id，如果有就重复利用
            String lastId = CookieUtil.getCookie(getRequest(), Constants.User.LAST_CAPTCHA_ID);
            String key;
            if(TextUtil.isEmpty(lastId)) {
                key = snowflakeIdWorker.nextId() + "";
            } else {
                key = lastId;
            }
            // 设置请求头为输出图片类型
            this.getResponse().setContentType("image/gif");
            this.getResponse().setHeader("Pragma", "No-cache");
            this.getResponse().setHeader("Cache-Control", "no-cache");
            this.getResponse().setDateHeader("Expires", 0);
            int captchaType = random.nextInt(3);
            Captcha targetCaptcha = null;
            // 设置Captcha风格类型
            int width = 120;
            int height = 40;
            if (captchaType == 0) {
                //英文与数字类型
                targetCaptcha = new SpecCaptcha(width, height, 5);
            } else if (captchaType == 1) {
                //gif类型
                targetCaptcha = new GifCaptcha(width, height);
            } else {
                //算数类型
                targetCaptcha = new ArithmeticCaptcha(width, height);
                targetCaptcha.setLen(3);//几位数运算
            }

            // 设置字体样式
            int index = CAPTCHA_FONT_TYPES[random.nextInt(CAPTCHA_FONT_TYPES.length)];
            targetCaptcha.setFont(index);

            // 设置字体组合类型，纯数字/纯字母/字母数字混合
            targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);

            // 拿到验证码内容且英文转换为小写
            String content = targetCaptcha.text().toLowerCase();

            //将captchaKey写入Cookie用于后面检查
            CookieUtil.setUpCookie(getResponse(),Constants.User.LAST_CAPTCHA_ID,key);
            // 验证码存入redis
            // 删除时机：
            //      1.自然过期（10分钟后自己删除）
            //      2.用完后手动删除（get）
            redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, Constants.TimeValue.MIN_10);

            // 输出图片流
            targetCaptcha.out(this.getResponse().getOutputStream());
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 给用户邮箱发送验证码
     * 1. 第3步发送邮件非常耗时，我们应该异步发送邮件
     * 2. 三种场景：根据type参数决定
     * ① 注册邮箱(register)：如果邮箱已注册，提示该邮箱已经注册
     * ② 修改邮箱(update)：如果邮箱已注册，提示该邮箱已经注册
     * ③ 找回密码(forget)：如果邮箱没有注册过，提示该邮箱没有注册
     */
    @Override
    public ResponseResult sendEmail(String type, String emailAddress) {
        try {
            //判空
            if (emailAddress == null) {
                return ResponseResult.FAILED("邮箱地址不可以为空");
            }
            //根据类型，查询邮箱是否存在
            BlogUser userByEmail = MapperUtil.getMapper(BlogUserMapper.class).selectByEmail(emailAddress);
            if ("register".equals(type) || "update".equals(type)) {
                if (userByEmail != null) return ResponseResult.FAILED("该邮箱已经被注册");
            } else if ("forget".equals(type)) {
                if (userByEmail == null) return ResponseResult.FAILED("该邮箱未被注册");
            }
            //1. 防止暴力获取验证码
            String remoteAddr = this.getRequest().getRemoteAddr();
            if (remoteAddr != null) remoteAddr = remoteAddr.replaceAll(":", "_");
            //同一个用户IP一小时最多获取10次
            Object obj = redisUtil.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
            Integer ipSendTime = Integer.parseInt((obj == null ? "0" : obj).toString());
            if (ipSendTime > 10) {
                return ResponseResult.FAILED("请不要频繁获取邮箱验证码");
            }
            //同一个用户邮箱间隔30s才能获取一次
            Object hasEmailSend = redisUtil
                    .get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
            if (hasEmailSend != null) {
                return ResponseResult.FAILED("请不要频繁获取邮箱验证码");
            }
            //2. 检查邮箱地址是否正确
            if (!TextUtil.isEmailAddress(emailAddress)) {
                return ResponseResult.FAILED("邮箱地址格式不正确");
            }
            //3. 发送验证码（100000 ~ 999999）
            int code = random.nextInt(999999);
            if (code < 100000) code += 100000;
            //EmailSender.sendRegisterVerifyCode(String.valueOf(code),emailAddress);
            //异步发送邮件
            taskService.sendEmailVerifyCode(String.valueOf(code), emailAddress);
            //4. 做记录
            //发送记录
            redisUtil.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, String.valueOf(++ipSendTime), Constants.TimeValue.HOUR);
            redisUtil.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", Constants.TimeValue.SECOND_30);
            //code
            log.info("VerifyCode ============>" + String.valueOf(code));
            redisUtil.set(Constants.User.KEY_EMAIL_CONTENT + emailAddress, String.valueOf(code), Constants.TimeValue.MIN_10);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.FAILED("邮箱验证码发送失败，请稍后重试");
        }
        return ResponseResult.SUCCESS("邮箱验证码发送成功，请注意查看您的邮箱");
    }

    @Override
    public ResponseResult register(BlogUser blogUser, String emailCode, String captchaCode) {
        //1.检查是否可以注册
        // 检查当前用户名是否已注册
        if (TextUtil.isEmpty(blogUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        BlogUserMapper userMapper = MapperUtil.getMapper(BlogUserMapper.class);
        BlogUser userByUsername = userMapper.selectByUsername(blogUser.getUserName());
        if (userByUsername != null) {
            return ResponseResult.FAILED("该用户已经被注册");
        }
        //检查邮箱格式是否正确,检查该邮箱是否已注册
        if (TextUtil.isEmpty(blogUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        if (!TextUtil.isEmailAddress(blogUser.getEmail())) {
            return ResponseResult.FAILED("邮箱格式不正确");
        }
        BlogUser userByEmail = userMapper.selectByEmail(blogUser.getEmail());
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱已经被注册");
        }
        //检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CONTENT
                + blogUser.getEmail());
        if (TextUtil.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码已过期");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确");
        } else {
            redisUtil.del(Constants.User.KEY_EMAIL_CONTENT + blogUser.getEmail());
        }
        //从Cookie里拿captchaKey
        String captchaKey = CookieUtil.getCookie(getRequest(),Constants.User.LAST_CAPTCHA_ID);
        if (TextUtil.isEmpty(captchaKey)) {
            return ResponseResult.FAILED("请允许保留Cookie信息！");
        }
        //检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT
                + captchaKey);
        if (TextUtil.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码已过期");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("人类验证码不正确");
        } else {
            redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        }
        //2.对密码进行加密
        if (TextUtil.isEmpty(blogUser.getPassword())) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        blogUser.setPassword(passwordEncoder.encode(blogUser.getPassword()));
        //3.补全数据
        blogUser.setId(snowflakeIdWorker.nextId() + "");
        blogUser.setRegIp(this.getRequest().getRemoteAddr());
        blogUser.setLoginIp(this.getRequest().getRemoteAddr());
        blogUser.setUpdateTime(new Date());
        blogUser.setCreateTime(new Date());
        blogUser.setState("1");
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setRoles(Constants.User.ROLE_NORMAL);
        //4.保存到数据库中
        userMapper.insert(blogUser);
        CookieUtil.deleteCookie(getResponse(),Constants.User.LAST_CAPTCHA_ID);
        //5.返回结果
        return ResponseResult.GET(ResponseState.REGISTER_SUCCESS);
    }

    @Override
    public ResponseResult doLogin(String captcha, BlogUser blogUser, String from) {
        //from可能为空,设置默认值
        if (TextUtil.isEmpty(from) ||
                (!Constants.FROM_MOBILE.equals(from) && !Constants.FROM_PC.equals(from))) {
            from = Constants.FROM_MOBILE;
        }
        //从Cookie里拿captchaKey
        String captchaKey = CookieUtil.getCookie(getRequest(),Constants.User.LAST_CAPTCHA_ID);
        if (TextUtil.isEmpty(captchaKey)) {
            return ResponseResult.FAILED("请允许保留Cookie信息！");
        }
        //1.校验图灵验证码
        String captchaValue = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (!captcha.equals(captchaValue)) {
            return ResponseResult.FAILED("人类验证码不正确");
        }
        //验证成功，删除redis中的验证码
        redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        //2.校验账户名，可能是邮箱也有可能是用户名
        if (TextUtil.isEmpty(blogUser.getUserName())) {
            return ResponseResult.FAILED("账号名不可以为空");
        }
        if (TextUtil.isEmpty(blogUser.getPassword())) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        BlogUserMapper blogUserMapper = MapperUtil.getMapper(BlogUserMapper.class);
        BlogUser userByDB = blogUserMapper.selectByUsername(blogUser.getUserName());
        if (userByDB == null) {
            userByDB = blogUserMapper.selectByEmail(blogUser.getUserName());
        }
        if (userByDB == null) {
            return ResponseResult.FAILED("用户名或密码不正确");
        }
        //3.校验密码
        boolean matches = passwordEncoder.matches(blogUser.getPassword(), userByDB.getPassword());
        if (!matches) return ResponseResult.FAILED("用户名或密码不正确");
        //4.校验用户状态
        if (!"1".equals(userByDB.getState())) {
            return ResponseResult.ACCOUNT_DENIED();
        }
        //5.修改更新时间和登录ip
        userByDB.setLoginIp(getRequest().getRemoteAddr());
        userByDB.setUpdateTime(new Date());
        //6.生成token
        createToken(userByDB, from);
        CookieUtil.deleteCookie(getResponse(),Constants.User.LAST_CAPTCHA_ID);
        return ResponseResult.SUCCESS("登录成功");
    }

    /**
     * 通过携带的token_key检查用户是否有登录，如果登录了就返回用户信息
     *
     * @return
     */
    @Override
    public BlogUser checkBlogUser() {
        String tokenKey = null;
        try {
            tokenKey = CookieUtil.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
            if (TextUtil.isEmpty(tokenKey)) return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BlogUser blogUser = parseTokenByTokenKey(tokenKey);
        //解析出此请求是什么端的
        String from = tokenKey.startsWith(Constants.FROM_PC) ? Constants.FROM_PC : Constants.FROM_MOBILE;
        if (blogUser == null) {
            //解析token出错，可能是过期了
            //1.去MySQL查询refreshToken
            //如果是pc，就以pc的token_key来查
            //如果是mobile，就以mobile的token_key来查
            RefreshTokenMapper refreshTokenMapper = MapperUtil.getMapper(RefreshTokenMapper.class);
            RefreshToken refreshToken = null;
            if (Constants.FROM_PC.equals(from)) {
                refreshToken = refreshTokenMapper.selectByPCTokenKey(tokenKey);
            } else {
                refreshToken = refreshTokenMapper.selectByMobileTokenKey(tokenKey);
            }
            //2.如果不存在返回null
            if (refreshToken == null) {
                return null;
            }
            //3.如果存在，就解析refreshToken
            try {
                JwtUtil.parseToken(refreshToken.getRefreshToken());
                //4.如果refreshToken有效，就创建新的token，删掉旧refreshToken，创建新refreshToken
                BlogUser userByDB = MapperUtil.getMapper(BlogUserMapper.class).selectByPrimaryKey(refreshToken.getUserId());
                String newTokenKey = createToken(userByDB, from);
                //再次解析并返回blogUser
                return parseTokenByTokenKey(newTokenKey);
            } catch (Exception ex) {
                ex.printStackTrace();
                //5.如果refreshToken过期了，就返回null
                return null;
            }
        }
        return blogUser;
    }

    /**
     * 解析此token是从PC端来的还是移动端
     *
     * @param tokenKey
     * @return
     */
    private String parseFrom(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseToken(token);
                return ClaimsUtil.getFrom(claims);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private BlogUser parseTokenByTokenKey(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseToken(token);
                return ClaimsUtil.claimsToBlogUser(claims);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 创建Token，删除旧的refreshToken
     *
     * @param userByDB
     * @return
     */
    private String createToken(BlogUser userByDB, String from) {
        String oldTokenKey = CookieUtil.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        //根据来源删除refreshToken中对应的token_key
        RefreshTokenMapper refreshTokenMapper = MapperUtil.getMapper(RefreshTokenMapper.class);
        RefreshToken oldRefreshToken = refreshTokenMapper.selectByUserId(userByDB.getId());
        if (Constants.FROM_MOBILE.equals(from)) {
            if (oldRefreshToken != null) {
                //确保同端单点登录，删除redis里的老token
                redisUtil.del(Constants.User.KEY_TOKEN + oldRefreshToken.getMobileTokenKey());
            }
            refreshTokenMapper.updateMobileTokenKey(oldTokenKey);
        } else if (Constants.FROM_PC.equals(from)) {
            if (oldRefreshToken != null) {
                //确保同端单点登录，删除redis里的老token
                redisUtil.del(Constants.User.KEY_TOKEN + oldRefreshToken.getTokenKey());
            }
            refreshTokenMapper.updatePCTokenKey(oldTokenKey);
        }

        //生成新的Token，claims包含from
        Map<String, Object> claims = ClaimsUtil.blogUserToClaims(userByDB, from);
        String token = JwtUtil.getToken(claims);
        //6.将token的MD5值作为tokenKey写入Cookies
        //  将token保存到redis中（前端下次访问时就可以携带tokenKey从redis中获取token）
        String tokenKey = from + DigestUtils.md5DigestAsHex(token.getBytes());
        //保存Token到redis，有效期为2小时，key是tokenKey
        redisUtil.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValue.HOUR_2);
        //把tokenKey写到cookies里
        CookieUtil.setUpCookie(this.getResponse(), Constants.User.COOKIE_TOKEN_KEY, tokenKey);

        //先判断数据里有没有refreshToken,有就更新，没有就创建
        RefreshToken refreshToken = refreshTokenMapper.selectByUserId(userByDB.getId());
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setId(snowflakeIdWorker.nextId() + "");
            refreshToken.setCreateTime(new Date());
        }
        //不管是过期了还是新登录，都生成/更新refreshToken
        //7.生成refreshTokenValue并保存到数据库中
        String refreshTokenValue = JwtUtil.getRefreshToken(userByDB.getId(), Constants.TimeValue.MONTH);
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userByDB.getId());
        //判断来源
        if (Constants.FROM_PC.equals(from)) {
            refreshToken.setTokenKey(tokenKey);
        } else {
            refreshToken.setMobileTokenKey(tokenKey);
        }
        refreshToken.setUpdateTime(new Date());
        refreshTokenMapper.updateByPrimaryKey(refreshToken);
        return tokenKey;
    }

    /**
     * 用户能获取的用户信息：id，username，roles，avatar，state，sign
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getUserInfo(String userId) {
        BlogUser blogUser = MapperUtil.getMapper(BlogUserMapper.class).selectByPrimaryKey(userId);
        if (blogUser == null) {
            return ResponseResult.FAILED("用户不存在");
        }
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", blogUser.getId());
        userMap.put("userName", blogUser.getUserName());
        userMap.put("roles", blogUser.getRoles());
        userMap.put("avatar", blogUser.getAvatar());
        userMap.put("state", blogUser.getState());
        userMap.put("sign", blogUser.getSign());
        userMap.put("updateTime",blogUser.getUpdateTime());
        userMap.put("createTime",blogUser.getCreateTime());
        return ResponseResult.SUCCESS("查询成功").setData(userMap);
    }

    /**
     * 用户能修改的用户信息：username,password（单独修改）,email（单独修改）,avatar,sign
     *
     * @param userId
     * @param blogUser
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(String userId, BlogUser blogUser) {
        BlogUser userFromTokenKey = checkBlogUser();
        if (userFromTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        BlogUserMapper blogUserMapper = MapperUtil.getMapper(BlogUserMapper.class);
        BlogUser userFromDB = blogUserMapper.selectByPrimaryKey(userFromTokenKey.getId());
        if (!userFromDB.getId().equals(userId)) {
            return ResponseResult.PERMISSION_DENIED();
        }
        //username
        if (!TextUtil.isEmpty(blogUser.getUserName()) && !blogUser.getUserName().equals(userFromTokenKey.getUserName())) {
            BlogUser userByUserName = blogUserMapper.selectByUsername(blogUser.getUserName());
            if (userByUserName != null) {
                return ResponseResult.FAILED("该用户名已被注册");
            }
            userFromDB.setUserName(blogUser.getUserName());
        }
        //avatar
        if (!TextUtil.isEmpty(blogUser.getAvatar())) {
            userFromDB.setAvatar(blogUser.getAvatar());
        }
        //sign
        userFromDB.setSign(blogUser.getSign());
        //修改时间
        userFromDB.setUpdateTime(new Date());
        blogUserMapper.updateByPrimaryKey(userFromDB);
        //干掉redis里的token
        String tokenKey = CookieUtil.getCookie(this.getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        redisUtil.del(Constants.User.KEY_TOKEN + tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }

    @Override
    public ResponseResult checkEmail(String email) {
        BlogUser blogUser = MapperUtil.getMapper(BlogUserMapper.class).selectByEmail(email);
        return blogUser == null ? ResponseResult.FAILED("该邮箱未注册") : ResponseResult.SUCCESS("该邮箱已注册");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        BlogUser blogUser = MapperUtil.getMapper(BlogUserMapper.class).selectByUsername(userName);
        return blogUser == null ? ResponseResult.FAILED("该用户名未注册") : ResponseResult.SUCCESS("该用户名已注册");
    }

    /**
     * 删除用户（并不是真的删除而是修改用户状态），需要管理员权限
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        int result = MapperUtil.getMapper(BlogUserMapper.class).updateUserStateForDelete(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除成功");
        }
        return ResponseResult.FAILED("用户不存在");
    }

    /**
     * 获取用户列表，需要管理员权限
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult getUserList(int page, int size, String userName, String email) {
        //分页查询
        page = checkPage(page);
        size = checkSize(size);

        PageHelper.startPage(page, size, "create_time DESC");
        java.util.List<BlogUser> list = MapperUtil.getMapper(BlogUserMapper.class).selectAll(userName, email);
        PageInfo<BlogUser> pageInfo = new PageInfo<BlogUser>(list);
        return ResponseResult.SUCCESS("获取用户列表成功").setData(pageInfo);
    }

    /**
     * 修改密码
     *
     * @param verifyCode
     * @param blogUser
     * @return
     */
    @Override
    public ResponseResult updateUserPassword(String verifyCode, BlogUser blogUser) {
        //检查邮箱是否填写
        String email = blogUser.getEmail();
        if (TextUtil.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        //根据邮箱去redis里拿验证码
        String verifyCodeFromRedis = (String) redisUtil.get(Constants.User.KEY_EMAIL_CONTENT + email);
        if (verifyCodeFromRedis == null) return ResponseResult.FAILED("你还没有获取邮箱验证码");
        if (!verifyCodeFromRedis.equals(verifyCode)) return ResponseResult.FAILED("邮箱验证码错误");
        redisUtil.del(Constants.User.KEY_EMAIL_CONTENT + email);
        int result = MapperUtil.getMapper(BlogUserMapper.class)
                .updatePasswordByEmail(passwordEncoder.encode(blogUser.getPassword()), email);
        return result > 0 ? ResponseResult.SUCCESS("密码修改成功") : ResponseResult.FAILED("密码修改失败");
    }

    /**
     * 修改邮箱
     *
     * @param verifyCode
     * @param email
     * @return
     */
    @Override
    public ResponseResult updateUserEmail(String verifyCode, String email) {
        //确保用户已登录
        BlogUser blogUser = checkBlogUser();
        if (blogUser == null) return ResponseResult.ACCOUNT_NOT_LOGIN();
        //对比验证码，确保新邮箱属于当前用户
        String verifyCodeFromRedis = (String) redisUtil.get(Constants.User.KEY_EMAIL_CONTENT + email);
        if (TextUtil.isEmpty(verifyCodeFromRedis)) return ResponseResult.FAILED("你还没有获取邮箱验证码");
        if (!verifyCodeFromRedis.equals(verifyCode)) return ResponseResult.FAILED("邮箱验证码错误");
        //验证成功，删除redis中的邮箱验证码
        redisUtil.del(Constants.User.KEY_EMAIL_CONTENT + email);
        //可以修改
        int result = MapperUtil.getMapper(BlogUserMapper.class).updateEmailById(email, blogUser.getId());
        return result > 0 ? ResponseResult.SUCCESS("邮箱修改成功") : ResponseResult.FAILED("邮箱修改失败");
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Override
    public ResponseResult doLogout() {
        //拿到tokenKey
        String tokenKey = CookieUtil.getCookie(this.getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        if (TextUtil.isEmpty(tokenKey)) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除redis里的token，因为各端独立，可以删除
        redisUtil.del(Constants.User.KEY_TOKEN + tokenKey);
        //更新refreshToken,根据from置空对应tokenKey
        RefreshTokenMapper refreshTokenMapper = MapperUtil.getMapper(RefreshTokenMapper.class);
        if (Constants.FROM_PC.startsWith(tokenKey)) {
            refreshTokenMapper.updatePCTokenKey(tokenKey);
        } else {
            refreshTokenMapper.updateMobileTokenKey(tokenKey);
        }
        //删除Cookie里的token_key
        CookieUtil.deleteCookie(getResponse(), Constants.User.COOKIE_TOKEN_KEY);
        return ResponseResult.SUCCESS("退出登录成功");
    }

    /**
     * 单独做频繁请求处理
     *
     * @return
     */
    @Override
    public ResponseResult getPcLoginQrCode() {
        //尝试取出上一次的qrCode
        String lastQrCode = CookieUtil.getCookie(getRequest(), Constants.User.LAST_REQUEST_LOGIN_ID);
        long code;
        if (!TextUtil.isEmpty(lastQrCode)) {
//            //先把redis里的删除
//            redisUtil.del(Constants.User.KEY_PC_LOGIN_ID + lastQrCode);
//            //检查上一次的请求时间，如果太频繁，则直接返回
//            Object lastGetTime = redisUtil.get(Constants.User.LAST_REQUEST_LOGIN_ID + lastQrCode);
//            if (lastGetTime != null) {
//                return ResponseResult.FAILED("服务器繁忙，请稍后处理");
//            }
            code = Long.parseLong(lastQrCode);
        } else {
            //生成一个唯一id
            code = snowflakeIdWorker.nextId();
        }
        //2.保存到redis里，值为false，有效期为5分钟
        redisUtil.set(Constants.User.KEY_PC_LOGIN_ID + code,
                Constants.User.KEY_PC_LOGIN_STATE_FALSE, Constants.TimeValue.MIN_5);
        //3.返回结果
        Map<String, Object> result = new HashMap<>();
        String originalDomain = TextUtil.getDomain(this.getRequest());
        result.put("code", String.valueOf(code));
        result.put("url", originalDomain + "/portal/image/qr-code/" + String.valueOf(code));
        CookieUtil.setUpCookie(this.getResponse(), Constants.User.LAST_REQUEST_LOGIN_ID, String.valueOf(code));
//        //防止频繁请求
//        redisUtil.set(Constants.User.LAST_REQUEST_LOGIN_ID + String.valueOf(code),
//                "true", Constants.TimeValue.SECOND_10);
        return ResponseResult.SUCCESS("获取成功").setData(result);
    }

    /**
     * 检查二维码的登陆状态
     * 结果：
     * 1.登录成功（qrCode对应的值为userId）
     * 3.等待扫描（qrCode对应的值为false）
     * 3.二维码已经过期（qrCode对应的值为null）
     *
     * @param qrCode
     * @return
     */
    @Override
    public ResponseResult checkQrCodeLoginState(String qrCode) {
        //检查状态
        ResponseResult result = checkLoginState(qrCode);
        if (result != null) return result;
        //等待登录扫描
        Callable<ResponseResult> callable = new Callable<ResponseResult>() {
            @Override
            public ResponseResult call() {
                log.info("start waiting for scan ...");
                //先阻塞
                try {
                    countDownLatchManager.getLatch(qrCode).await(
                            Constants.User.QR_CODE_STATE_CHECK_WAITING_TIME, TimeUnit.SECONDS);
                    //收到状态更新的通知，我们就再次检查qrCode对应的状态
                    log.info("start check login state ...");
                    ResponseResult checkResult = checkLoginState(qrCode);
                    if (checkResult != null) return checkResult;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //完事后，删除对应的latch
                    countDownLatchManager.deleteLatch(qrCode);
                }
                //超时则返回等待扫描
                return ResponseResult.WAITING_FOR_SCAN();
            }
        };
        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.WAITING_FOR_SCAN();
    }

    /**
     * 调用是被PC端轮询调用的
     *
     * @param qrCode
     * @return
     */
    private ResponseResult checkLoginState(String qrCode) {
        String loginState = (String) redisUtil.get(Constants.User.KEY_PC_LOGIN_ID + qrCode);
        log.info("loginState ===> " + loginState);
        //二维码过时
        if (loginState == null) {
            return ResponseResult.QR_CODE_DEPRECATE();
        }
        //扫码成功，有内容（userId）且不为false
        if (!TextUtil.isEmpty(loginState) && !Constants.User.KEY_PC_LOGIN_STATE_FALSE.equals(loginState)) {
            //创建token，也就是走PC端的登录
            BlogUser userFromDB = MapperUtil.getMapper(BlogUserMapper.class).selectByPrimaryKey(loginState);
            if (userFromDB == null) return ResponseResult.QR_CODE_DEPRECATE();
            createToken(userFromDB, Constants.FROM_PC);
            CookieUtil.deleteCookie(getResponse(),Constants.User.LAST_REQUEST_LOGIN_ID);
            //登录成功
            return ResponseResult.LOGIN_SUCCESS();
        }
        return null;
    }

    @Override
    public ResponseResult updateQrCodeLoginState(String qrCode) {
        //1.检查用户是否登录
        BlogUser blogUser = checkBlogUser();
        if (blogUser == null) return ResponseResult.ACCOUNT_NOT_LOGIN();
        //2.改变qrCode对应的值为
        redisUtil.set(Constants.User.KEY_PC_LOGIN_ID + qrCode, blogUser.getId());
        //  通知正在等待扫描的
        countDownLatchManager.onPhoneDoLogin(qrCode);
        //3.返回结果
        return ResponseResult.LOGIN_SUCCESS();
    }

    @Override
    public ResponseResult parseToken() {
        BlogUser blogUser = checkBlogUser();
        if (blogUser == null) {
            return ResponseResult.FAILED("用户未登录");
        }
        return ResponseResult.SUCCESS("获取用户成功").setData(blogUser);
    }

    @Override
    public ResponseResult resetUserPassword(String userId, String password) {
        BlogUserMapper blogUserMapper = MapperUtil.getMapper(BlogUserMapper.class);
        BlogUser blogUser = blogUserMapper.selectByPrimaryKey(userId);
        if (blogUser == null) {
            return ResponseResult.FAILED("用户不存在！");
        }
        String encode = passwordEncoder.encode(password);
        int i = blogUserMapper.updatePasswordByUserId(userId, encode);
        return i == 1 ? ResponseResult.SUCCESS("密码重置成功") : ResponseResult.FAILED("密码重置失败！");
    }

    @Override
    public ResponseResult checkEmailCode(String email, String emailCode, String captchaCode) {
        //检查人类验证码
        String captchaId = CookieUtil.getCookie(getRequest(), Constants.User.LAST_CAPTCHA_ID);
        String captcha = (String)redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaId);
        if(!captchaCode.equals(captcha)) {
            return ResponseResult.FAILED("人类验证码不正确！");
        }
        //检查邮箱验证码
        String redisVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CONTENT + email);
        if(!emailCode.equals(redisVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确！");
        }
        //返回结果
        return ResponseResult.SUCCESS("邮箱验证码输入正确！");
    }

    public ResponseResult registerCount() {
        int count = MapperUtil.getMapper(BlogUserMapper.class).selectRegisterCount();
        return ResponseResult.SUCCESS("注册用户数查询成功！").setData(count);
    }
}