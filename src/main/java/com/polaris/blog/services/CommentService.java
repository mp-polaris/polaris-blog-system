package com.polaris.blog.services;

import com.polaris.blog.pojo.Comment;
import com.polaris.blog.response.ResponseResult;

public interface CommentService {
    ResponseResult postComment(Comment comment);

    ResponseResult getCommentListByArticleId(String articleId, int page, int size);

    ResponseResult getCommentList(int page, int size);

    ResponseResult deleteComment(String commentId);

    ResponseResult topComment(String commentId);

    ResponseResult getCommentCount();
}
