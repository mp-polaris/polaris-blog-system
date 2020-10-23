package com.polaris.blog.controller.portal;

import com.polaris.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/portal/app")
public class AppAPI {
    /**
     * 应用下载
     *  http://localhost:8081/portal/app/===767339627471175680
     */
    @GetMapping("/{code}")
    public void downloadAppFromThirdPartyScan(@PathVariable("code")String code,
                                              HttpServletRequest request,
                                              HttpServletResponse response){
        //TODO:直接下载
    }

    /**
     * 应用检查更新
     */
}
