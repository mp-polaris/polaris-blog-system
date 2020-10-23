package com.polaris.blog.controller.admin;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.WebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 网站信息API
 */
@RestController
@RequestMapping("/admin/web_size_info")
public class WebSizeInfoAdminAPI {
    @Autowired
    private WebSizeInfoService webSizeInfoService;

    /**
     * 获取网站标题
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return webSizeInfoService.getWebSizeTitle();
    }

    /**
     * 修改网站标题
     * @param title
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/title")
    public ResponseResult updateWebSizeTitle(@RequestParam("title")String title){
        return webSizeInfoService.updateWebSizeTitle(title);
    }

    /**
     * 获取网站的seo信息
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/seo")
    public ResponseResult getWebSizeSeoInfo(){
        return webSizeInfoService.getSeoInfo();
    }

    /**
     * 修改网站的seo信息
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/seo")
    public ResponseResult updateWebSizeSeoInfo(
            @RequestParam("keywords")String keywords,
            @RequestParam("description")String description){
        return webSizeInfoService.updateSeoInfo(keywords,description);
    }

    /**
     * 获取网站的统计信息
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return webSizeInfoService.getWebSizeViewCount();
    }

}
