package com.polaris.blog.controller.portal;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/image")
public class ImagePortalAPI {
    @Autowired
    private ImageService imageService;

    /**
     * 获取图片
     * @param imageUrl
     * @return
     */
    @GetMapping("/{imageUrl}")
    public void getImage(@PathVariable("imageUrl")String imageUrl){
        imageService.getImage(imageUrl);
    }

    /**
     * 获取二维码
     * @param code
     */
    @GetMapping("/qr-code/{code}")
    public void getQrCodeImage(@PathVariable("code")String code){
        imageService.getQrCodeImage(code);
    }
}
