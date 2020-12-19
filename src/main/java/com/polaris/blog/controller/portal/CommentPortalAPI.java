package com.polaris.blog.controller.portal;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.Comment;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalAPI {
    @Autowired
    private CommentService commentService;

    /**
     * 提交评论
     * @return
     */
    @CheckTooFrequentCommit
    @PostMapping()
    public ResponseResult postComment(@RequestBody Comment comment){
        return commentService.postComment(comment);
    }

    /**
     * 获取评论列表
     * @param articleId
     * @return
     */
    @GetMapping("/list/{articleId}/{page}/{size}")
    public ResponseResult getCommentList(@PathVariable("articleId")String articleId,
                                         @PathVariable("page")int page,
                                         @PathVariable("size")int size){
        return commentService.getCommentListByArticleId(articleId,page,size);
    }

    /**
     * 删除评论，只允许删除自己的
     * @param commentId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId")String commentId){
        return commentService.deleteComment(commentId);
    }
}
