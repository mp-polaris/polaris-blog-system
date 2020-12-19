package com.polaris.blog.controller.portal;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CategoryService;
import com.polaris.blog.services.FriendLinkService;
import com.polaris.blog.services.LooperService;
import com.polaris.blog.services.WebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalAPI {
    @Autowired
    private FriendLinkService friendLinkService;
    @Autowired
    private LooperService looperService;
    @Autowired
    private WebSizeInfoService webSizeInfoService;

    /**
     * 获取网站友情链接
     * @return
     */
    @GetMapping("/friend_links")
    public ResponseResult getWebSizeFriendLinkList(){
        return friendLinkService.getFriendLinkList();
    }

    /**
     * 获取网站轮播图
     * @return
     */
    @GetMapping("/loops")
    public ResponseResult getWebSizeLooperList(){
        return looperService.getLooperList();
    }

    /**
     * 获取网站访问量
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return webSizeInfoService.getWebSizeViewCount();
    }

    /**
     * 获取网站标题
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return webSizeInfoService.getWebSizeTitle();
    }

    /**
     * 获取网站SEO信息
     * @return
     */
    @GetMapping("/seo")
    public ResponseResult getWebSizeSeo(){
        return webSizeInfoService.getSeoInfo();
    }

    @PutMapping("/view_count")
    public void updateWebSizeViewCount(){
        webSizeInfoService.updateViewCount();
    }
}
