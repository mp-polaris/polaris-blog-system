package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polaris.blog.dao.ArticleMapper;
import com.polaris.blog.dao.CommentMapper;
import com.polaris.blog.dao.LabelMapper;
import com.polaris.blog.pojo.Article;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.Comment;
import com.polaris.blog.pojo.Label;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CommentService;
import com.polaris.blog.services.UserService;
import com.polaris.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.soap.Text;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CommentServiceImpl extends BaseService implements CommentService {
   @Autowired
   private UserService userService;
   @Autowired
   private SnowflakeIdWorker idWorker;
   @Autowired
   private RedisUtil redisUtil;
   @Autowired
   private Gson gson;

    @Override
    public ResponseResult postComment(Comment comment) {
        BlogUser blogUser = userService.checkBlogUser();
        if(blogUser == null) return ResponseResult.ACCOUNT_NOT_LOGIN();
        //判空，检查
        String articleId = comment.getArticleId();
        if (TextUtil.isEmpty(articleId)) return ResponseResult.FAILED("文章id不能为空");
        Article article = MapperUtil.getMapper(ArticleMapper.class).selectByPrimaryKey(articleId);
        if(article == null) return ResponseResult.FAILED("文章不存在");

        String commentContent = comment.getCommentContent();
        if (TextUtil.isEmpty(commentContent)) return ResponseResult.FAILED("文章内容不能为空");
        //补全内容
        comment.setId(idWorker.nextId() + "");
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setUserAvatar(blogUser.getAvatar());
        comment.setUserName(blogUser.getUserName());
        comment.setUserId(blogUser.getId());
        //入库
        MapperUtil.getMapper(CommentMapper.class).insert(comment);
        //清除对应文章的评论缓存(即下一次要重新生成该文章评列表论第一页的缓存)
        redisUtil.del(Constants.Comment.KEY_COMMENT_FIRST_PAGE_CACHE + comment.getArticleId());
        return ResponseResult.SUCCESS("评论成功");
    }

    /**
     * 评论的排序策略：
     *      最基本的策略：升序和降序，置顶的一定要在最前面
     *      其他策略：刚发表的文章，单位时间内会排在前面，之后就会按点赞量和发表时间排序
     * @param articleId
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult getCommentListByArticleId(String articleId, int page, int size) {
        if (articleId == null) return ResponseResult.SUCCESS("文章Id不能为空");
        page = checkPage(page);
        size = checkSize(size);

        List<Comment> comments = null;
        PageHelper.startPage(page,size,"state DESC,create_time DESC");

        //先从缓存中获取
        String cacheJson = (String) redisUtil.get(Constants.Comment.KEY_COMMENT_FIRST_PAGE_CACHE + articleId);
        if (!TextUtil.isEmpty(cacheJson) && page == 1) {
            comments = gson.fromJson(cacheJson,new TypeToken<List<Comment>>(){
            }.getType());
            log.info("comment list from redis...");
        } else {
            comments = MapperUtil.getMapper(CommentMapper.class).selectAllByArticleId(articleId);
        }

        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        //如果是第一页就保存一份到缓存
        if(page == 1) {
            redisUtil.set(Constants.Comment.KEY_COMMENT_FIRST_PAGE_CACHE + articleId, gson.toJson(comments),Constants.TimeValue.HOUR);
        }
        return ResponseResult.SUCCESS("评论列表获取成功").setData(pageInfo);
    }

    @Override
    public ResponseResult getCommentList(int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        PageHelper.startPage(page,size,"state DESC,create_time DESC");
        List<Comment> comments = MapperUtil.getMapper(CommentMapper.class).selectAll();
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        return ResponseResult.SUCCESS("评论列表获取成功").setData(pageInfo);
    }

    @Override
    public ResponseResult topComment(String commentId) {
        CommentMapper commentMapper = MapperUtil.getMapper(CommentMapper.class);
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if(comment == null) return ResponseResult.FAILED("评论不存在");
        String state = comment.getState();
        if (Constants.Comment.STATE_PUBLISH.equals(state)) {
            comment.setState(Constants.Comment.STATE_TOP);
            commentMapper.updateByPrimaryKey(comment);
            return ResponseResult.SUCCESS("评论置顶成功");
        } else if(Constants.Comment.STATE_TOP.equals(state)){
            comment.setState(Constants.Comment.STATE_PUBLISH);
            commentMapper.updateByPrimaryKey(comment);
            return ResponseResult.SUCCESS("评论取消置顶成功");
        }
        return ResponseResult.SUCCESS("此评论状态非法");
    }

    @Override
    public ResponseResult deleteComment(String commentId) {
        BlogUser blogUser = userService.checkBlogUser();
        if(blogUser == null) return ResponseResult.ACCOUNT_NOT_LOGIN();
        //把评论找出来，比对用户
        CommentMapper commentMapper = MapperUtil.getMapper(CommentMapper.class);
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if(comment == null) return ResponseResult.FAILED("评论不存在");
        if (blogUser.getId().equals(comment.getUserId()) ||
                Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            commentMapper.deleteByPrimaryKey(commentId);
            return ResponseResult.SUCCESS("评论删除成功");
        }
        return ResponseResult.PERMISSION_DENIED();
    }

}
