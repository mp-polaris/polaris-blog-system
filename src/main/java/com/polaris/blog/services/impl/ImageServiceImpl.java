package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.CategoryMapper;
import com.polaris.blog.dao.ImageMapper;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.Category;
import com.polaris.blog.pojo.Image;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ImageService;
import com.polaris.blog.services.UserService;
import com.polaris.blog.utils.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl extends BaseService implements ImageService {
    //上传的路径，通过配置文件配置
    @Value("${polaris.blog.image.save-path}")
    private String imagePath;
    @Value("${polaris.blog.image.max-size}")
    private Long imageMaxSize;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    /**
     * 上传的路径：通过配置文件配置
     * 上传的内容：命名可以用id，每一天用一个文件夹保存
     * 保存记录到数据库里 ID/用户ID/url/原名称/存储路径/状态/创建日期/更新日期
     *
     * @param file
     * @return
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file, String category) {
        //判空
        if (file == null) return ResponseResult.FAILED("图片不可以为空");
        //判断文件类型，这里只支持图片格式： png / jpg / gif
        String contentType = file.getContentType();
        log.info("contentType ==========>" + contentType);
        if (TextUtil.isEmpty(contentType)) return ResponseResult.FAILED("文件格式错误");
        //获取相关数据，比如文件类型，文件名称
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename ==========>" + originalFilename);
        String type = null;
        if (Constants.ImageType.TYPE_PREFIX_PNG.equals(contentType)
                && originalFilename.endsWith(Constants.ImageType.TYPE_PNG)) {
            type = Constants.ImageType.TYPE_PNG;
        } else if (Constants.ImageType.TYPE_PREFIX_JPEG.equals(contentType)
                && (originalFilename.endsWith(Constants.ImageType.TYPE_JPEG)
                || originalFilename.endsWith(Constants.ImageType.TYPE_JPG))) {
            type = Constants.ImageType.TYPE_JPG;
        } else if (Constants.ImageType.TYPE_PREFIX_GIF.equals(contentType)
                && originalFilename.endsWith(Constants.ImageType.TYPE_GIF)) {
            type = Constants.ImageType.TYPE_GIF;
        }
        if (type == null) return ResponseResult.FAILED("不支持此图片类型");
        //限制文件的大小,通过配置文件配置
        log.info("imageSize ===> " + file.getSize() + "; imageMaxSize ====> " + imageMaxSize);
        if (file.getSize() > imageMaxSize) {
            return ResponseResult.FAILED("图片最大仅支持" + imageMaxSize / (1024 * 2) + "MB");
        }
        //创建图片的保存目录，文件夹和文件命名规则：配置目录/日期/类型/ID.类型
        Long currentMillions = System.currentTimeMillis();
        String currentDay = new SimpleDateFormat("yyyy_MM_dd").format(currentMillions);
        log.info("currentDay ======>" + currentDay);
        String pathOfDay = imagePath + File.separator + currentDay;
        File pathOfDayFile = new File(pathOfDay);
        if (!pathOfDayFile.exists()) pathOfDayFile.mkdirs();//判断日期文件夹是否存在
        String targetName = String.valueOf(idWorker.nextId()); //图片新名字，随机数,作为数据库id字段
        String targetPath = pathOfDay +
                File.separator + type + File.separator + targetName + "." + type;
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists()) targetFile.mkdirs();//判断类型文件夹是否存在
        try {
            if (!targetFile.exists()) targetFile.createNewFile();//判断文件是否存在
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("targetFile ==========>" + targetFile);
        //保存文件
        try {
            file.transferTo(targetFile);
            //返回结果：包含：图片名称，访问路径
            Map<String, String> result = new HashMap<>();
            String resultPath = currentMillions + "_" + targetName + "." + type;
            result.put("id", resultPath);
            result.put("originalName", originalFilename);
            //保存记录到数据库
            Image image = new Image();
            image.setId(targetName);
            image.setUserId(userService.checkBlogUser().getId());
            image.setUrl(resultPath);
            image.setPath(targetFile.getPath());
            image.setContentType(contentType);
            image.setOriginalName(originalFilename);
            image.setState("1");
            if (category != null) {
                image.setCategory(category);
            }
            image.setCreateTime(new Date());
            image.setUpdateTime(new Date());
            MapperUtil.getMapper(ImageMapper.class).insert(image);
            return ResponseResult.SUCCESS("图片上传成功").setData(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("图片上传失败，请稍后重试");
    }

    @Override
    public void getImage(String imageUrl) {
        //配置的目录已知
        //  需要日期
        String[] paths = imageUrl.split("_");
        String dayValue = paths[0];
        String format = new SimpleDateFormat("yyyy_MM_dd").format(Long.parseLong(dayValue));
        log.info("getImage() format =======>" + format);
        //  需要ID
        String imageName = paths[1];
        //  需要类型
        String type = imageName.substring(imageName.length() - 3);
        //组成目标路径
        String targetPath = imagePath + File.separator + format + File.separator
                + type + File.separator + imageName;
        log.info("getImage() targetPath =======>" + targetPath);
        File file = new File(targetPath);
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            this.getResponse().setContentType(Constants.ImageType.PREFIX + type);
            os = this.getResponse().getOutputStream();
            //读
            fis = new FileInputStream(file);
            //写
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = fis.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResponseResult getImageList(int page, int size, String category) {
        //参数检查
        page = checkPage(page);
        size = checkSize(size);

        //ps：如何如果要做多人博客，可以获取用户信息，根据用户id查自己的列表

        PageHelper.startPage(page, size, "create_time DESC");
        List<Image> list = null;
        if (category == null) {
            list = MapperUtil.getMapper(ImageMapper.class).selectAllByState();
        } else {
            list = MapperUtil.getMapper(ImageMapper.class).selectAllByStateAndCategory(category);
        }
        PageInfo<Image> pageInfo = new PageInfo<>(list);
        return ResponseResult.SUCCESS("图片列表查询成功").setData(pageInfo);
    }

    /**
     * 删除的是图片的数据库信息，而非真正删除了图片
     * @param imageId
     * @return
     */
    @Override
    public ResponseResult deleteImage(String imageId) {
        int result = MapperUtil.getMapper(ImageMapper.class).updateStateByImageId(imageId);
        return result > 0 ? ResponseResult.SUCCESS("图片删除成功") : ResponseResult.FAILED("图片删除失败");
    }

    /**
     * 二维码生成方式：
     * 方式一：可以简单的是一个code，也就是传进来的这个
     * 如果是用我们自己写的app来扫描，可以识别并解析，请求对应的接口
     * 如果是第三方的可以识别但是不能解析，只能显示这个code
     * 方式二：我们应该提供的是一个app下载地址+code
     * 如果我们自己的app扫到，切割后面的内容拿到code进行解析
     * 如果是第三方qpp扫描，它是个网址，就会访问下载app的地址
     * 实现：
     * APP_DOWNLOAD_PATH/code
     *
     * @param code
     */
    @Override
    public void getQrCodeImage(String code) {
        //检查code是否过期
        String loginState = (String) redisUtil.get(Constants.User.KEY_PC_LOGIN_ID + code);
        if (TextUtil.isEmpty(loginState)) {
            //TODO: 返回一张图片显示已经过期
            return;
        }
        HttpServletResponse response = this.getResponse();
        HttpServletRequest request = this.getRequest();

        //获取域名（第三方app扫描需要知道我们的网址去下载app）
        String originalDomain = TextUtil.getDomain(request);

        //生成二维码
        String content = originalDomain + Constants.APP_DOWNLOAD_PATH + "===" + code;
        log.info("qr-code content ==> " + content);
        byte[] result = QrCodeUtils.encodeQRCode(content);
        response.setContentType(QrCodeUtils.RESPONSE_CONTENT_TYPE);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(result);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
