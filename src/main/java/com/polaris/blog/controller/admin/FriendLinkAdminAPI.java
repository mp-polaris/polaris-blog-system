package com.polaris.blog.controller.admin;//package com.polaris.blog.controller.admin;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.FriendLink;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 友情链接API
 */
@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkAdminAPI {
    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 添加友情链接
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendLink){
        return friendLinkService.addFriendLink(friendLink);
    }

    /**
     * 获取友情链接
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(
            @PathVariable("friendLinkId")String friendLinkId){
        return friendLinkService.getFriendLink(friendLinkId);
    }

    /**
     * 获取友情链接列表
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult getFriendLinkList(){
        return friendLinkService.getFriendLinkList();
    }

    /**
     * 修改友情链接
     * @param friendLinkId
     * @param friendLink
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{friendLink_id}")
    public ResponseResult updateFriendLink(@PathVariable("friendLink_id")String friendLinkId,
                                           @RequestBody FriendLink friendLink){
        return friendLinkService.updateFriendLink(friendLinkId,friendLink);
    }

    /**
     * 删除友情链接
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{friendLink_id}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLink_id")String friendLinkId){
        return friendLinkService.deleteFriendLink(friendLinkId);
    }

}
