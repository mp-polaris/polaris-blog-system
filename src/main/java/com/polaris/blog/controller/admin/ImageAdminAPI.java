package com.polaris.blog.controller.admin;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 管理中心 -> 图片API
 */
@RestController
@RequestMapping("/admin/image")
public class ImageAdminAPI {
    @Autowired
    private ImageService imageService;

    /**
     * 上传图片
     * 关于文件上传：
     * ① 比较常用的是对象存储，如阿里云的
     * ② 使用 Nginx + fastDFS。fastDFS负责文件上传，Nginx负责文件访问
     * ③ 简单方式
     *
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping("/{category}")
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file,
                                      @PathVariable(value = "category", required = false) String category) {
        return imageService.uploadImage(file, category);
    }

    /**
     * 获取图片列表
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getImageList(@PathVariable("page") int page,
                                       @PathVariable("size") int size,
                                       @RequestParam(value = "category", required = false) String category) {
        return imageService.getImageList(page, size, category);
    }

    /**
     * 删除图片
     *
     * @param imageId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return imageService.deleteImage(imageId);
    }
}
