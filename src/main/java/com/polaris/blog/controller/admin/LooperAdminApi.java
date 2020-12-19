package com.polaris.blog.controller.admin;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.Looper;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.LooperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 轮播图API
 */
@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {
    @Autowired
    private LooperService looperService;

    /**
     * 添加轮播图
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addLooper(@RequestBody Looper looper){
        return looperService.addLooper(looper);
    }

    /**
     * 获取轮播图
     * @param looperId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{looperId}")
    public ResponseResult getLooper(@PathVariable("looperId")String looperId){
        return looperService.getLooper(looperId);
    }

    /**
     * 获取轮播图列表
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult getLooperList(){
        return looperService.getLooperList();
    }

    /**
     * 修改轮播图
     * @param looperId
     * @param looper
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{looperId}")
    public ResponseResult updateLooper(@PathVariable("looperId")String looperId,
                                      @RequestBody Looper looper){
        return looperService.updateLooper(looperId,looper);
    }

    /**
     * 删除轮播图
     * @param looperId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLooper(@PathVariable("looperId")String looperId){
        return looperService.deleteLooper(looperId);
    }
}
