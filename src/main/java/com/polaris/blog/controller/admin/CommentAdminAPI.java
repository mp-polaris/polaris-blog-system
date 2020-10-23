package com.polaris.blog.controller.admin;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 评论API
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentAdminAPI {
    @Autowired
    private CommentService commentService;

    /**
     * 获取评论
     * @param commentId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{commentId}")
    public ResponseResult getComment(@PathVariable("commentId")String commentId){
        return null;
    }

    /**
     * 获取所有文章的评论列表
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getCommentList(@PathVariable("page")int page,
                                         @PathVariable("size")int size){
        return commentService.getCommentList(page,size);
    }

    /**
     * 置顶评论
     * @param commentId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId")String commentId){
        return commentService.topComment(commentId);
    }

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId")String commentId){
        return commentService.deleteComment(commentId);
    }

}
